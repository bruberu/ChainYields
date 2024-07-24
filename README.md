## Chain Yield Finder

Are you looking to find what kind of elements are most common in fission byproducts? Have you realized
that https://www-nds.iaea.org/wimsd/fpyield.htm is missing a gazillion isotopes?
Look no further, as this project hopes to solve that question.

The chain yield of an isotope (or element) is its fission yield after the fission byproducts have had a certain amount
of time to decay. This figure can be especially useful for determining the composition of spent nuclear fuel, as might
be required for research for a video game or an article. This project can be used to (approximately) calculate these
chain yields and
output them for various common fission reactions.

### How to use

If you don't see your preferred data in the tables below, you will have to deal with some of the Java code behind this
directly. Load this into a Java IDE (IntelliJ is pretty good), change lines 13 and 14 of Main.java to have your
preferred isotope (as long as it's in the nds.iaea.org API) and year cutoff (the minimum number of years required for an
isotope to be considered stable enough).

It will prompt you occasionally to give the symbol of certain elements, such as silver (Ag), europium (Eu), and so on.
Make sure you give the right symbol for the element!
You may also see a log for a decay mode not being implemented, if you choose a particularly exotic fissionable isotope
to start with. If so, just send an issue here.

It may take up to a few minutes to get the results on the first time, as at least a hundred CSV files will be downloaded
from the nds.iaea.org API. Afterwards, it will output a table of fission yields in percentages into the console.

### Tables

#### U235

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000097                | 0.000019                 |
| He             | 0.001711                | 0.001789                 |
| Se             | 0.005055                | 0.005055                 |
| Br             | 0.001840                | 0.001840                 |
| Kr             | 0.047378                | 0.038116                 |
| Rb             | 0.027455                | 0.036717                 |
| Sr             | 0.090214                | 0.062869                 |
| Y              | 0.046984                | 0.046969                 |
| Zr             | 0.312708                | 0.340043                 |
| Nb             | 0.000029                | 0.000000                 |
| Mo             | 0.244724                | 0.244777                 |
| Tc             | 0.061318                | 0.061312                 |
| Ru             | 0.114259                | 0.113209                 |
| Rh             | 0.031015                | 0.031015                 |
| Pd             | 0.014444                | 0.015494                 |
| Ag             | 0.000170                | 0.000170                 |
| Cd             | 0.000224                | 0.000224                 |
| Sn             | 0.000604                | 0.000604                 |
| Sb             | 0.000000                | 0.000000                 |
| Te             | 0.020873                | 0.020873                 |
| I              | 0.008110                | 0.008109                 |
| Xe             | 0.214957                | 0.214957                 |
| Cs             | 0.190205                | 0.161984                 |
| Ba             | 0.069914                | 0.098134                 |
| La             | 0.063342                | 0.063342                 |
| Ce             | 0.130974                | 0.121733                 |
| Pr             | 0.058469                | 0.058469                 |
| Nd             | 0.197249                | 0.206490                 |
| Pm             | 0.013308                | 0.000006                 |
| Sm             | 0.026693                | 0.039170                 |
| Eu             | 0.001621                | 0.002299                 |
| Gd             | 0.000050                | 0.000197                 |

#### Pu239

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000127                | 0.000025                 |
| He             | 0.002205                | 0.002307                 |
| Se             | 0.003918                | 0.003918                 |
| Br             | 0.001723                | 0.001723                 |
| Kr             | 0.020387                | 0.016145                 |
| Rb             | 0.010310                | 0.014552                 |
| Sr             | 0.032207                | 0.022598                 |
| Y              | 0.016783                | 0.016777                 |
| Zr             | 0.187172                | 0.196769                 |
| Nb             | 0.000022                | 0.000000                 |
| Mo             | 0.229152                | 0.229192                 |
| Tc             | 0.061856                | 0.061850                 |
| Ru             | 0.193911                | 0.183185                 |
| Rh             | 0.069455                | 0.069455                 |
| Pd             | 0.146052                | 0.156777                 |
| Ag             | 0.016594                | 0.016594                 |
| Cd             | 0.005497                | 0.005497                 |
| In             | 0.000247                | 0.000247                 |
| Sn             | 0.005691                | 0.005676                 |
| Sb             | 0.001804                | 0.001151                 |
| Te             | 0.037088                | 0.037756                 |
| I              | 0.018562                | 0.018562                 |
| Xe             | 0.227601                | 0.227601                 |
| Cs             | 0.206410                | 0.175809                 |
| Ba             | 0.064938                | 0.095538                 |
| La             | 0.059650                | 0.059650                 |
| Ce             | 0.109386                | 0.103039                 |
| Pr             | 0.052022                | 0.052021                 |
| Nd             | 0.156851                | 0.163199                 |
| Pm             | 0.012177                | 0.000006                 |
| Sm             | 0.036614                | 0.047257                 |
| Eu             | 0.005172                | 0.005429                 |
| Gd             | 0.002512                | 0.003783                 |
| Tb             | 0.000116                | 0.000116                 |

#### Pu241

| Element Symbol | Fission Yield (2 Years) | Fission Yield (31 Years) |
|----------------|-------------------------|--------------------------|
| H              | 0.000126                | 0.000025                 |
| He             | 0.001875                | 0.001976                 |
| Se             | 0.001359                | 0.001359                 |
| Br             | 0.000474                | 0.000474                 |
| Kr             | 0.015817                | 0.012791                 |
| Rb             | 0.008344                | 0.011370                 |
| Sr             | 0.024435                | 0.017281                 |
| Y              | 0.012203                | 0.012198                 |
| Zr             | 0.145526                | 0.152670                 |
| Nb             | 0.000017                | 0.000000                 |
| Mo             | 0.191712                | 0.191744                 |
| Tc             | 0.056141                | 0.056136                 |
| Ru             | 0.204132                | 0.188905                 |
| Rh             | 0.065330                | 0.065330                 |
| Pd             | 0.227287                | 0.242513                 |
| Ag             | 0.029637                | 0.029637                 |
| Cd             | 0.010464                | 0.010464                 |
| In             | 0.001130                | 0.001130                 |
| Sn             | 0.007716                | 0.007691                 |
| Sb             | 0.003548                | 0.002104                 |
| Te             | 0.028653                | 0.030122                 |
| I              | 0.018305                | 0.018305                 |
| Xe             | 0.220748                | 0.220748                 |
| Cs             | 0.196190                | 0.167065                 |
| Ba             | 0.067017                | 0.096141                 |
| La             | 0.059414                | 0.059414                 |
| Ce             | 0.111943                | 0.104989                 |
| Pr             | 0.048944                | 0.048944                 |
| Nd             | 0.166104                | 0.173059                 |
| Pm             | 0.013426                | 0.000006                 |
| Sm             | 0.042398                | 0.054139                 |
| Eu             | 0.005309                | 0.005707                 |
| Gd             | 0.006158                | 0.007438                 |
| Tb             | 0.001117                | 0.001117                 |
| Dy             | 0.001230                | 0.001230                 |