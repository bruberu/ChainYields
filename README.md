## Chain Yield Finder

Are you looking to find what kind of elements are most common in fission byproducts? Have you realized
that https://www-nds.iaea.org/wimsd/fpyield.htm is missing a gazillion isotopes?
Look no further, as this project hopes to solve that question.

The chain yield of an isotope (or element) is its fission yield after the fission byproducts have had a certain amount
of time to decay. This figure can be especially useful for determining the composition of spent nuclear fuel, as might
be required for research for a video game or an article. This project can be used to calculate these chain yields and
output them for various common fission reactions.

### How to use

If you don't see your preferred data in the tables below, you will have to deal with some of the Java code behind this
directly. Load this into a Java IDE (IntelliJ is pretty good), change lines 13 and 14 of Main.java to have your
preferred isotope (as long as it's in the nds.iaea.org API) and year cutoff (the minimum number of years of its half-life required for an
isotope to be considered stable enough).

It will prompt you occasionally to give the symbol of certain elements, such as silver (Ag), europium (Eu), and so on.
Make sure you give the right symbol for the element!
You may also see a log for a decay mode not being implemented, if you choose a particularly exotic fissionable isotope
to start with. If so, just send an issue here.

It may take up to a few minutes to get the results on the first time, as at least a hundred CSV files will be downloaded
from the nds.iaea.org API. Afterwards, it will output a table of fission yields into the console. These yields will add up to just under 2, which is fine, since you get, well, two atoms per fission, and the extremely rare fission byproducts have been filtered out. 

### Tables

#### U235

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000108                | negligible               |
| He             | 0.001700                | 0.001808                 |
| Se             | 0.005055                | 0.005055                 |
| Br             | 0.001840                | 0.001840                 |
| Kr             | 0.048886                | 0.036432                 |
| Rb             | 0.025947                | 0.038401                 |
| Sr             | 0.092892                | 0.035646                 |
| Y              | 0.046962                | 0.046962                 |
| Zr             | 0.310028                | 0.367275                 |
| Mo             | 0.244777                | 0.244777                 |
| Tc             | 0.061318                | 0.061318                 |
| Ru             | 0.113209                | 0.113209                 |
| Rh             | 0.031015                | 0.031015                 |
| Pd             | 0.015494                | 0.015494                 |
| Ag             | 0.000170                | 0.000170                 |
| Cd             | 0.000224                | 0.000224                 |
| Sn             | 0.000604                | 0.000604                 |
| Te             | 0.020873                | 0.020873                 |
| I              | 0.008110                | 0.008110                 |
| Xe             | 0.214957                | 0.214957                 |
| Cs             | 0.192936                | 0.132307                 |
| Ba             | 0.067183                | 0.127812                 |
| La             | 0.063342                | 0.063342                 |
| Ce             | 0.121733                | 0.121733                 |
| Pr             | 0.058469                | 0.058469                 |
| Nd             | 0.206489                | 0.206489                 |
| Pm             | 0.022315                | negligible               |
| Sm             | 0.017750                | 0.040065                 |
| Eu             | 0.001607                | 0.001408                 |
| Gd             | negligible              | 0.000199                 |

#### Pu239

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000142                | negligible               |
| He             | 0.002190                | 0.002332                 |
| Se             | 0.003918                | 0.003918                 |
| Br             | 0.001723                | 0.001723                 |
| Kr             | 0.021078                | 0.015374                 |
| Rb             | 0.009620                | 0.015323                 |
| Sr             | 0.033148                | 0.013030                 |
| Y              | 0.016775                | 0.016775                 |
| Zr             | 0.186221                | 0.206339                 |
| Mo             | 0.229192                | 0.229192                 |
| Tc             | 0.061857                | 0.061857                 |
| Ru             | 0.183185                | 0.183185                 |
| Rh             | 0.069455                | 0.069455                 |
| Pd             | 0.156778                | 0.156778                 |
| Ag             | 0.016594                | 0.016594                 |
| Cd             | 0.005497                | 0.005497                 |
| In             | 0.000247                | 0.000247                 |
| Sn             | 0.005676                | 0.005676                 |
| Sb             | 0.002248                | 0.001151                 |
| Te             | 0.036660                | 0.037757                 |
| I              | 0.018562                | 0.018562                 |
| Xe             | 0.227601                | 0.227601                 |
| Cs             | 0.209371                | 0.143628                 |
| Ba             | 0.061976                | 0.127719                 |
| La             | 0.059650                | 0.059650                 |
| Ce             | 0.103039                | 0.103039                 |
| Pr             | 0.052021                | 0.052021                 |
| Nd             | 0.163198                | 0.163198                 |
| Pm             | 0.020418                | negligible               |
| Sm             | 0.028491                | 0.048909                 |
| Eu             | 0.005490                | 0.003764                 |
| Gd             | 0.002076                | 0.003801                 |
| Tb             | 0.000116                | 0.000116                 |

#### Pu241

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000141                | negligible               |
| He             | 0.001860                | 0.002001                 |
| Se             | 0.001359                | 0.001359                 |
| Br             | 0.000474                | 0.000474                 |
| Kr             | 0.016310                | 0.012240                 |
| Rb             | 0.007851                | 0.011920                 |
| Sr             | 0.025135                | 0.010158                 |
| Y              | 0.012196                | 0.012196                 |
| Zr             | 0.144817                | 0.159794                 |
| Mo             | 0.191744                | 0.191744                 |
| Tc             | 0.056142                | 0.056142                 |
| Ru             | 0.188905                | 0.188905                 |
| Rh             | 0.065330                | 0.065330                 |
| Pd             | 0.242513                | 0.242513                 |
| Ag             | 0.029637                | 0.029637                 |
| Cd             | 0.010464                | 0.010464                 |
| In             | 0.001130                | 0.001130                 |
| Sn             | 0.007691                | 0.007691                 |
| Sb             | 0.004510                | 0.002103                 |
| Te             | 0.027716                | 0.030123                 |
| I              | 0.018305                | 0.018305                 |
| Xe             | 0.220748                | 0.220748                 |
| Cs             | 0.199009                | 0.136436                 |
| Ba             | 0.064198                | 0.126771                 |
| La             | 0.059414                | 0.059414                 |
| Ce             | 0.104989                | 0.104989                 |
| Pr             | 0.048944                | 0.048944                 |
| Nd             | 0.173058                | 0.173058                 |
| Pm             | 0.022512                | negligible               |
| Sm             | 0.033441                | 0.055954                 |
| Eu             | 0.005619                | 0.003880                 |
| Gd             | 0.005718                | 0.007457                 |
| Tb             | 0.001117                | 0.001117                 |
| Dy             | 0.001230                | 0.001230                 |
