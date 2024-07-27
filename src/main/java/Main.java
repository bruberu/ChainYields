import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.function.Consumer;

public class Main {
    public static String ISOTOPE = "235u";
    public static double YEAR_CUTOFF = 2;

    public static Map<Integer, String> ELEMENTS = new HashMap<>();
    public static Map<Isotope, IsotopeData> ISOTOPES = new HashMap<>();

    public static boolean cleanup = false;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java -jar isotope.jar <isotope> <year> -c");
        } else {
            if(Objects.equals(args[0], "-c")) {
                cleanupDirs();
                return;
            }
            ISOTOPE = args[0];
        }

        if (args.length > 1) {
            try {
                YEAR_CUTOFF = Double.parseDouble(args[1]);
            }
            catch (NumberFormatException e) {
                checkClean(1, args);
            }
        }

        if(args.length > 2) {
            checkClean(2, args);
        }

        Map<IsotopeData, Double> initProducts = getFissions(ISOTOPE);
        Map<IsotopeData, Double> products = new HashMap<>();

        for (Map.Entry<IsotopeData, Double> product : initProducts.entrySet()) {
            product.getKey().calculateChainDecays(YEAR_CUTOFF * 365.25 * 24 * 60 * 60);

            for (Map.Entry<IsotopeData, Double> entry : product.getKey().chainDecays.entrySet()) {
                products.put(entry.getKey(), products.getOrDefault(entry.getKey(), 0.0) + entry.getValue() * product.getValue());
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

        if(cleanup) {
            cleanupDirs();
        }
    }

    public static void cleanupDirs() throws IOException {
        FileUtils.deleteDirectory(new File("./fission"));
        FileUtils.deleteDirectory(new File("./base"));
    }

    public static void checkClean(int index, String[] args) {
        if(Objects.equals(args[index], "-c")) {
            cleanup = true;
        } else {
            throw new IllegalArgumentException("To enable cleanup, use -c!");
        }
    }

    public enum FissionHeaders {
        z, a, elem, un1, un2, un3, meta, independent_thermal_fy
    }

    public static Map<IsotopeData, Double> getFissions(String fissile) throws IOException {

        new File("./fission").mkdirs();

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

        Iterable<CSVRecord> recordElements = CSVFormat.DEFAULT.builder().setHeader(FissionHeaders.class).setSkipHeaderRecord(true).build().parse(br);

        Map<IsotopeData, Double> products = new HashMap<>();
        for (CSVRecord record : recordElements) {
            int protons = Integer.parseInt(record.get(FissionHeaders.z));
            ELEMENTS.put(protons, record.get(FissionHeaders.elem));
        }

        br.close();
        br = new BufferedReader(new FileReader(f));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setHeader(FissionHeaders.class).setSkipHeaderRecord(true).build().parse(br);

        for (CSVRecord record : records) {
            String thermal = record.get(FissionHeaders.independent_thermal_fy);
            if (!thermal.equals("independent_thermal_fy") && !thermal.isEmpty()) {
                double thermalFy = Double.parseDouble(record.get(FissionHeaders.independent_thermal_fy));
                if (thermalFy >= 0.0001) {
                    int massNumber = Integer.parseInt(record.get(FissionHeaders.a));
                    int protons = Integer.parseInt(record.get(FissionHeaders.z));

                    Isotope isotope = new Isotope(protons, massNumber);
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
        Map<IsotopeData, Double> decays;
        Isotope isotope;

        Map<IsotopeData, Double> chainDecays;

        public IsotopeData(Isotope isotope) throws IOException {
            this.isotope = isotope;
            decays = new HashMap<>();

            new File("./base").mkdirs();

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
                if (record.get("half_life_sec").isEmpty() || record.get("half_life_sec").equals("STABLE")) {
                    continue;
                }
                halfLife = Double.parseDouble(record.get("half_life_sec"));

                if (!isUnstable())
                    continue;

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
            ISOTOPES.put(isotope, this);
        }

        public boolean isUnstable() {
            return halfLife < 1000 * 60 * 60 * 24 * 365.25;
        }

        public void calculateChainDecays(double sec) {
            Tree<IsotopeData> tree = getTree();

            chainDecays = new HashMap<>();
            decayRates(chainDecays, tree, new ArrayList<>(), sec, 1);
        }

        public void decayRates(Map<IsotopeData, Double> rates, Tree<IsotopeData> current, List<Double> currentRates, double time, double factor) {
            List<Double> copy = new ArrayList<>(currentRates);
            copy.add(decayConstant(current.data.halfLife));
            if (current.data.isUnstable()) {
                for (Tree<IsotopeData> entry : current.children) {
                    decayRates(rates, entry, copy, time, current.data.decays.get(entry.data) * factor);
                }
            }
            if (rates.containsKey(current.data)) {
                rates.put(current.data, rates.get(current.data) + bateman(copy, time) * factor);
            } else {
                rates.put(current.data, bateman(copy, time) * factor);
            }
        }

        public Tree<IsotopeData> getTree() {
            Tree<IsotopeData> tree = new Tree<>(this);
            for (Map.Entry<IsotopeData, Double> entry : decays.entrySet()) {
                tree.children.add(entry.getKey().getTree());
            }
            return tree;
        }

        public String toString() {
            return isotope.toString();
        }
    }

    public static class IsotopeQuantity {
        public IsotopeData isotope;
        public double amount;

        public IsotopeQuantity(IsotopeData isotope, double amount) {
            this.isotope = isotope;
            this.amount = amount;
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

    public static IsotopeData doDecay(String decay, Isotope isotope) throws IOException {
        return new IsotopeData(calcDecay(decay, isotope));
    }

    public static Isotope calcDecay(String decay, Isotope isotope) {
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


    public static double decayConstant(double halfLife) {
        return Math.max(0, Math.log(2) / halfLife);
    }

    public static double bateman(List<Double> decayRates, double time) {
        double product = 1;
        for (int i = 0; i < decayRates.size() - 1; i++) {
            product *= decayRates.get(i);
        }
        double sum = 0;
        for (int i = 0; i < decayRates.size(); i++) {
            double sectionProduct = 1;
            for (int j = 0; j < decayRates.size(); j++) {
                if (i != j) {
                    double diff = decayRates.get(j) - decayRates.get(i);
                    if (Math.abs(diff) < 0.0000000001) {
                        diff = 0.0000000002 * Math.signum(diff);
                        decayRates.set(Math.max(i, j), decayRates.get(Math.max(i, j)) + diff);
                    }
                    sectionProduct /= diff; // Should not blow up due to the forbidden thing
                }
            }

            sum += sectionProduct * Math.exp(-decayRates.get(i) * time);
        }
        return sum * product;
    }

    public static class Tree<T> {
        T data;
        List<Tree<T>> children;

        public Tree(T data) {
            this.data = data;
            this.children = new LinkedList<>();
        }


    }
}