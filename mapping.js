var continents = {
XAD:"EEE",
ALA:"EEE",
ALB:"EEE",
AND:"EEE",
AUT:"EEE",
BLR:"EEE",
BEL:"EEE",
BIH:"EEE",
BGR:"EEE",
HRV:"EEE",
CYP:"EEE",
CZE:"EEE",
DNK:"EEE",
EST:"EEE",
FRO:"EEE",
FIN:"EEE",
FRA:"EEE",
DEU:"EEE",
GIB:"EEE",
GRC:"EEE",
GGY:"EEE",
HUN:"EEE",
ISL:"EEE",
IRL:"EEE",
IMN:"EEE",
ITA:"EEE",
JEY:"EEE",
KAZ:"EEE",
XKO:"EEE",
LVA:"EEE",
LIE:"EEE",
LTU:"EEE",
LUX:"EEE",
MLT:"EEE",
MDA:"EEE",
MCO:"EEE",
MNE:"EEE",
NLD:"EEE",
MKD:"EEE",
NOR:"EEE",
POL:"EEE",
PRT:"EEE",
ROU:"EEE",
RUS:"EEE",
SMR:"EEE",
SRB:"EEE",
SVK:"EEE",
SVN:"EEE",
ESP:"EEE",
SJM:"EEE",
SWE:"EEE",
CHE:"EEE",
UKR:"EEE",
GBR:"EEE",
VAT:"EEE",

AFG:"ABB",
ARM:"ABB",
AZE:"ABB",
BHR:"ABB",
BGD:"ABB",
BTN:"ABB",
IOT:"ABB",
BRN:"ABB",
KHM:"ABB",
CCK:"ABB",
CHN:"ABB",
CXR:"ABB",
GEO:"ABB",
HKG:"ABB",
IND:"ABB",
IDN:"ABB",
IRN:"ABB",
IRQ:"ABB",
ISR:"ABB",
JPN:"ABB",
JOR:"ABB",
KWT:"ABB",
KGZ:"ABB",
LAO:"ABB",
LBN:"ABB",
MAC:"ABB",
MYS:"ABB",
MDV:"ABB",
MNG:"ABB",
MMR:"ABB",
NPL:"ABB",
PRK:"ABB",
OMN:"ABB",
PAK:"ABB",
PSE:"ABB",
PHL:"ABB",
QAT:"ABB",
SAU:"ABB",
SGP:"ABB",
KOR:"ABB",
LKA:"ABB",
SYR:"ABB",
TWN:"ABB",
TJK:"ABB",
THA:"ABB",
TLS:"ABB",
TUR:"ABB",
TKM:"ABB",
ARE:"ABB",
UZB:"ABB",
VNM:"ABB",
YEM:"ABB",
ZNC:"ABB",

DZA:"FFF",
AGO:"FFF",
BDI:"FFF",
BEN:"FFF",
BWA:"FFF",
BFA:"FFF",
BDI:"FFF",
CPV:"FFF",
CMR:"FFF",
CAF:"FFF",
TCD:"FFF",
COM:"FFF",
COG:"FFF",
COD:"FFF",
CIV:"FFF",
DJI:"FFF",
EGY:"FFF",
GNQ:"FFF",
ERI:"FFF",
ETH:"FFF",
ATF:"FFF",
GAB:"FFF",
GMB:"FFF",
GHA:"FFF",
GIN:"FFF",
GNB:"FFF",
KEN:"FFF",
LSO:"FFF",
LBR:"FFF",
LBY:"FFF",
MDG:"FFF",
MWI:"FFF",
MLI:"FFF",
MRT:"FFF",
MUS:"FFF",
MYT:"FFF",
MAR:"FFF",
MOZ:"FFF",
NAM:"FFF",
NER:"FFF",
NGA:"FFF",
COD:"FFF",
REU:"FFF",
RWA:"FFF",
STP:"FFF",
SEN:"FFF",
SYC:"FFF",
SLE:"FFF",
SOM:"FFF",
ZAF:"FFF",
SSD:"FFF",
SHN:"FFF",
SDN:"FFF",
SWZ:"FFF",
TZA:"FFF",
TGO:"FFF",
TUN:"FFF",
UGA:"FFF",
COD:"FFF",
ZMB:"FFF",
ZWE:"FFF",
ESH:"FFF",

ABW:"NNN",
AIA:"NNN",
ATG:"NNN",
BHS:"NNN",
BRB:"NNN",
BLZ:"NNN",
BMU:"NNN",
BES:"NNN",
VGB:"NNN",
CAN:"NNN",
CYM:"NNN",
XCL:"NNN",
CRI:"NNN",
CUB:"NNN",
CUW:"NNN",
DMA:"NNN",
DOM:"NNN",
SLV:"NNN",
GRL:"NNN",
GRD:"NNN",
GLP:"NNN",
GTM:"NNN",
HTI:"NNN",
HND:"NNN",
JAM:"NNN",
MTQ:"NNN",
MEX:"NNN",
MSR:"NNN",
ANT:"NNN",
CUW:"NNN",
NIC:"NNN",
PAN:"NNN",
PRI:"NNN",
BLM:"NNN",
KNA:"NNN",
LCA:"NNN",
MAF:"NNN",
SPM:"NNN",
VCT:"NNN",
SXM:"NNN",
TTO:"NNN",
TCA:"NNN",
USA:"NNN",
UMI:"NNN",
VIR:"NNN",

ARG:"SRR",
BOL:"SRR",
BRA:"SRR",
CHL:"SRR",
COL:"SRR",
ECU:"SRR",
FLK:"SRR",
GUF:"SRR",
GUY:"SRR",
PRY:"SRR",
PER:"SRR",
SUR:"SRR",
URY:"SRR",
VEN:"SRR",

ASM:"UUU",
AUS:"UUU",
COK:"UUU",
FJI:"UUU",
PYF:"UUU",
GUM:"UUU",
KIR:"UUU",
MHL:"UUU",
FSM:"UUU",
NRU:"UUU",
NCL:"UUU",
NZL:"UUU",
NIU:"UUU",
NFK:"UUU",
MNP:"UUU",
PLW:"UUU",
PNG:"UUU",
PCN:"UUU",
WSM:"UUU",
SLB:"UUU",
TKL:"UUU",
TON:"UUU",
TUV:"UUU",
VUT:"UUU",
WLF:"UUU",

ATA:"XXX",
BVT:"XXX",
HMD:"XXX",
SGS:"XXX",
}

import fs from "fs"
import {JSDOM} from "jsdom"

function groupByContinent(svgFilePath) {
    // Read the SVG file
    fs.readFile(svgFilePath, 'utf-8', (err, data) => {
        if (err) {
            console.error('Error reading SVG file:', err);
            return;
        }

        // Create a virtual DOM with JSDOM
        const dom = new JSDOM(data);
        const document = dom.window.document;

        // Select the root <svg> element
        const svgElement = document.querySelector('svg');

        // Group <g> elements representing countries by continent
        const world = {}; // Renamed continents object to 'world'
        svgElement.querySelectorAll('g').forEach(countryGroup => {
            const countryId = countryGroup.getAttribute('id');
            if (countryId!= null){
                const continentId = continents[countryId.replace(/\d/g, "")] || "XXX"; // Remove numbers from country ID to get continent ID
                if (!world[continentId]) {
                    world[continentId] = document.createElementNS('http://www.w3.org/2000/svg', 'g');
                    world[continentId].setAttribute('id', continentId);
                }
                world[continentId].innerHTML += countryGroup.outerHTML;
            }else{

            console.log(countryId,countryGroup.outerHTML)
            }
        });

        // Create new <g> elements for each continent
        svgElement.innerHTML = '';
        for (const continentId in world) {
            svgElement.appendChild(world[continentId]);
        }

        // Output the modified SVG
        fs.writeFile(svgFilePath, svgElement.outerHTML, 'utf-8', err => {
            if (err) {
                console.error('Error writing to SVG file:', err);
                return;
            }
            console.log('SVG file updated successfully.');
        });
    });
}


// Example usage:
const svgFileBasePath = "./app/src/main/assets/" ;
groupByContinent(svgFileBasePath+ "webmercator01.svg");
groupByContinent(svgFileBasePath+ "aeqd01.svg");
groupByContinent(svgFileBasePath+ "loxim01.svg");