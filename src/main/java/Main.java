import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Main {
    public static String ISOTOPE = "241pu";
    public static double YEAR_CUTOFF = 31;

    public static Map<Integer, String> ELEMENTS = new HashMap<>();
    public static Map<Isotope, IsotopeData> ISOTOPES = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Map<IsotopeData, Double> products = getFissions(ISOTOPE);

        while (true) {
            boolean updated = false;
            Map<IsotopeData, Double> toAdd = new HashMap<>();
            List<IsotopeData> toRemove = new ArrayList<>();
            for (Map.Entry<IsotopeData, Double> product : products.entrySet()) {
                if (product.getKey().isUnstable()) {
                    updated = true;
                    for (Map.Entry<Isotope, Double> decay : product.getKey().decays.entrySet()) {
                        if (decay.getValue() > 0.0001) {
                            toAdd.put(getIsotope(decay.getKey()), product.getValue() * decay.getValue());
                        }
                    }

                    toRemove.add(product.getKey());
                }
            }

            for (IsotopeData toRemove1 : toRemove) {
                products.remove(toRemove1);
            }
            products.putAll(toAdd);
            if (!updated) {
                break;
            }
        }

        double[] z = new double[100];

        for (Map.Entry<IsotopeData, Double> product : products.entrySet()) {
            z[product.getKey().isotope.protons] += product.getValue();
        }

        for (int i = 0; i < z.length; i++) {
            if (z[i] > 0) {
                System.out.println("| " + ELEMENTS.get(i) + " | " + String.format(Locale.ENGLISH, "%6f", z[i]) + " |");
            }
        }
    }

    public enum FissionHeaders {
        z, a, elem, un1, un2, un3, meta, independent_thermal_fy
    }

    public static Map<IsotopeData, Double> getFissions(String fissile) throws IOException {
        File f = new File("./fission/fission" + fissile + ".csv");

        if (!f.exists()) {
            f.createNewFile();
            URL url = new URL("https://nds.iaea.org/relnsd/v1/data?fields=independent_fy&parents=" + fissile);

            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:77.0) Gecko/20100101 Firefox/77.0");
            Files.copy(con.getInputStream(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        BufferedReader br = new BufferedReader(new FileReader(f));

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setHeader(FissionHeaders.class).setSkipHeaderRecord(true).build().parse(br);

        Map<IsotopeData, Double> products = new HashMap<>();
        for (CSVRecord record : records) {
            String thermal = record.get(FissionHeaders.independent_thermal_fy);
            if (!thermal.equals("independent_thermal_fy") && !thermal.isEmpty()) {
                double thermalFy = Double.parseDouble(record.get(FissionHeaders.independent_thermal_fy));
                if (thermalFy >= 0.0001) {
                    int protons = Integer.parseInt(record.get(FissionHeaders.z));
                    int massNumber = Integer.parseInt(record.get(FissionHeaders.a));

                    Isotope isotope = new Isotope(protons, massNumber);
                    ELEMENTS.put(protons, record.get(FissionHeaders.elem));
                    ISOTOPES.put(isotope, new IsotopeData(isotope));
                    products.put(getIsotope(isotope), thermalFy);
                }
            }
        }
        br.close();
        return products;
    }

    public static class IsotopeData {
        double halfLife = Double.MAX_VALUE;
        Map<Isotope, Double> decays;
        Isotope isotope;

        public IsotopeData(Isotope isotope) throws IOException {
            this.isotope = isotope;
            decays = new HashMap<>();
            File f = new File("./base/base" + isotope + ".csv");

            if (!f.exists()) {
                f.createNewFile();
                URL url = new URL("https://nds.iaea.org/relnsd/v1/data?fields=ground_states&nuclides=" + isotope);

                URLConnection con = url.openConnection();
                con.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:77.0) Gecko/20100101 Firefox/77.0");

                InputStream initialStream = con.getInputStream();
                OutputStream outStream = Files.newOutputStream(f.toPath());
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = initialStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            BufferedReader br = new BufferedReader(new FileReader(f));

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(br);

            for (CSVRecord record : records) {
                if (record.get("half_life_sec").isEmpty()) {
                    continue;
                }
                halfLife = Double.parseDouble(record.get("half_life_sec"));

                if (!record.get("decay_1").isEmpty() && !record.get("decay_1_%").isEmpty()) {
                    decays.put(doDecay(record.get("decay_1"), isotope), Double.parseDouble(record.get("decay_1_%")) / 100);
                }

                if (!record.get("decay_2").isEmpty() && !record.get("decay_2_%").isEmpty()) {
                    double chance = Double.parseDouble(record.get("decay_2_%")) / 100;
                    // They normalize things weirdly sometimes...
                    if (decays.containsValue(1.)) {
                        decays.replaceAll((k, v) -> 1 - chance);
                    }
                    decays.put(doDecay(record.get("decay_2"), isotope), chance);
                }

                if (!record.get("decay_3").isEmpty() && !record.get("decay_3_%").isEmpty()) {
                    decays.put(doDecay(record.get("decay_3"), isotope), Double.parseDouble(record.get("decay_3_%")) / 100);
                }
            }

            br.close();
        }

        public boolean isUnstable() {
            return halfLife < 60 * 60 * 24 * 365 * YEAR_CUTOFF;
        }
    }

    public static class Isotope {
        public int protons;
        public int massNumber;

        public Isotope(int protons, int massNumber) {
            this.protons = protons;
            this.massNumber = massNumber;
        }

        public String toString() {
            return massNumber + getElement(protons);
        }
    }

    public static String getElement(int protons) {
        if (ELEMENTS.containsKey(protons)) {
            return ELEMENTS.get(protons);
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the name of the element " + protons + ": ");
            String name = scanner.nextLine();
            ELEMENTS.put(protons, name);
            return name;
        }
    }


    public static IsotopeData getIsotope(Isotope isotope) {
        return ISOTOPES.computeIfAbsent(isotope, (isotope1) -> {
            try {
                return new IsotopeData(isotope1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Isotope doDecay(String decay, Isotope isotope) {
        if (decay.equals("A")) {
            return new Isotope(isotope.protons - 2, isotope.massNumber - 4);
        } else if (decay.equals("B-")) {
            return new Isotope(isotope.protons + 1, isotope.massNumber);
        } else if (decay.equals("B-N")) {
            return new Isotope(isotope.protons + 1, isotope.massNumber - 1);
        } else if (decay.equals("B-2N")) {
            return new Isotope(isotope.protons + 1, isotope.massNumber - 2);
        } else if (decay.equals("B+P")) {
            return new Isotope(isotope.protons - 2, isotope.massNumber - 1);
        } else if (decay.equals("2B-")) {
            return new Isotope(isotope.protons + 2, isotope.massNumber);
        } else if (decay.equals("B+")) {
            return new Isotope(isotope.protons - 1, isotope.massNumber);
        } else if (decay.equals("EC+B+")) {
            return new Isotope(isotope.protons - 1, isotope.massNumber);
        }
        System.out.println("Decay not yet implemented for " + decay);
        return null;
    }
}