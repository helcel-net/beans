#!/bin/node

import {readFileSync,writeFileSync,existsSync} from 'fs'
import area from '@turf/area'
import * as turf from '@turf/turf'
import * as path from 'path';
import { JSDOM } from 'jsdom';


const countries =
[
  "AFG","XAD","ALA","ALB","DZA","ASM","AND","AGO","AIA","ATG","ARG","ARM","ABW","AUS","AUT","AZE",
  "BHS","BHR","BGD","BRB","BLR","BEL","BLZ","BEN","BMU","BTN","BOL","BES","BIH","BWA","BVT","BRA", "IOT","VGB","BRN","BGR","BFA","BDI","KHM",
  "CMR","CAN","CPV","XCA","CYM","CAF","TCD","CHL","CHN","CXR","XCL","CCK","COL","COM","COK","CRI","CIV","HRV","CUB","CUW","CYP","CZE","COD",
  "DNK","DJI","DMA","DOM","ECU","EGY","SLV","GNQ","ERI","EST","ETH","FLK","FRO","FJI","FIN","FRA","GUF","PYF","ATF",
  "GAB","GMB","GEO","DEU","GHA","GIB","GRC","GRL","GRD","GLP","GUM","GTM","GGY","GIN","GNB","GUY","HTI","HMD","HND","HUN",
  "ISL","IND","IDN","IRN","IRQ","IRL","IMN","ISR","ITA","JAM","JPN","JEY","JOR","KAZ","KEN","KIR","XKO","KWT","KGZ",
  "LAO","LVA","LBN","LSO","LBR","LBY","LIE","LTU","LUX","SXM",
  "MKD","MDG","MWI","MYS","MDV","MLI","MLT","MHL","MTQ","MRT","MUS","MYT","MEX","FSM","MDA","MCO","MNG","MNE","MSR","MAR","MOZ","MMR",
  "NAM","NRU","NPL","NLD","NCL","NZL","NIC","NER","NGA","NIU","NFK","PRK","ZNC","MNP","NOR","OMN",
  "PAK","PLW","PSE","PAN","PNG","PRY","PER","PHL","PCN","POL","PRT","PRI","QAT","COG","REU","ROU","RUS","RWA","BLM","MAF",
  "SHN","KNA","LCA","SPM","VCT","WSM","SMR","STP","SAU","SEN","SRB","SYC","SLE","SGP","SVK","SVN","SLB","SOM","ZAF","SGS","KOR","SSD","ESP",
  "LKA","SDN","SUR","SJM","SWZ","SWE","CHE","SYR","TWN","TJK","TZA","THA","TLS","TGO","TKL","TON","TTO","TUN","TUR","TKM","TCA","TUV","UGA",
  "UKR","ARE","GBR","USA","UMI","URY","UZB","VUT","VAT","VEN","VNM","VIR","WLF","ESH","YEM","ZMB","ZWE"
]


const groups = {
  "EEE":["ALA","ALB","AND","AUT","BLR","BEL","BIH","BGR","HRV","CYP","CZE","DNK","EST","FIN","FRA","DEU","GIB","GGY","GRC","HUN","ISL","IRL","IMN","JEY","ITA","KAZ","XKO","LVA","LIE","LTU","LUX","MLT","MDA","MCO","MNE","NLD","MKD","NOR","SJM","POL","PRT","ROU","RUS","SMR","SRB","SVK","SVN","ESP","SWE","CHE","UKR","GBR","VAT","XAD"],
  "ABB":["AFG","ARM","AZE","BHR","BGD","BTN","BRN","KHM","CHN","GEO","HKG","IND","IDN","IRN","IRQ","ISR","JPN","JOR","KWT","KGZ","LAO","LBN","MAC","MYS","MDV","MNG","MMR","NPL","PRK","OMN","PAK","PSE","PHL","QAT","SAU","SGP","KOR","LKA","SYR","TWN","TJK","THA","TLS","TUR","TKM","ARE","UZB","VNM","YEM","ZNC"],
  "FFF":["DZA","AGO","BDI","BEN","BWA","BFA","BDI","CPV","CMR","CAF","TCD","COM","COG","COD","CIV","DJI","EGY","GNQ","ERI","SWZ","ETH","GAB","GMB","GHA","GIN","GNB","KEN","LSO","LBR","LBY","MDG","MWI","MLI","MRT","MUS","MYT","MAR","MOZ","NAM","NER","NGA","COD","REU","RWA","STP","SEN","SYC","SLE","SOM","ZAF","SSD","SHN","SDN","TZA","TGO","TUN","UGA","COD","ZMB","ZWE","ESH"],
  "NNN":["ABW","AIA","ATG","BHS","BRB","BLZ","BMU","VGB","CAN","CYM","CRI","CUB","CUW","DMA","DOM","SLV","GRL","GRD","GLP","GTM","HTI","HND","JAM","MTQ","MEX","MSR","ANT","CUW","NIC","PAN","PRI","KNA","LCA","MAF","SPM","VCT","TTO","TCA","USA","XCL"],
  "SRR":["ARG","BOL","BRA","CHL","COL","ECU","FLK","GUF","GUY","PRY","PER","SUR","URY","VEN"],
  "UUU":["ASM","AUS","COK","FJI","PYF","GUM","KIR","MHL","FSM","NRU","NCL","NZL","NIU","NFK","MNP","PLW","PNG","PCN","SLB","TKL","TON","TUV","VUT","WLF"],
  "XXX":[
      "ATA", // Antarctica: not in any other region
      "BES",// Bonaire, Sint Eustatius and Saba: special municipalities of the Netherlands in the Caribbean
      "BVT",// Bouvet Island: an uninhabited territory of Norway in the South Atlantic
      "IOT",// British Indian Ocean Territory: a British overseas territory in the Indian Ocean
      "CXR",// Christmas Island: an Australian external territory in the Indian Ocean
      "CCK",// Cocos (Keeling) Islands: an Australian external territory in the Indian Ocean
      "FRO",// Faroe Islands: an autonomous region of Denmark
      "ATF",// French Southern and Antarctic Lands: a territory of France located in the southern Indian Ocean
      "HMD",// Heard Island and McDonald Islands: an uninhabited Australian external territory in the southern Indian Ocean
      "BLM",// Saint Barthélemy: an overseas collectivity of France in the Caribbean
      "WSM",// Samoa: an independent island nation in the South Pacific
      "SXM",// Sint Maarten: a constituent country of the Kingdom of the Netherlands in the Caribbean
      "SGS",// South Georgia and the South Sandwich Islands: a British overseas territory in the southern Atlantic Ocean
      "UMI",// United States Minor Outlying Islands: a collection of nine insular areas of the United States
      "VIR",// United States Virgin Islands: an unincorporated territory of the United States in the Caribbean
  ]
}

var dict0 = {}
var dict1 = {}

const formatStr = (str)=> str.replace(/(?<!\b\w\u00E0-\u00FC)\B[A-Z\u00C0-\u00DC]|,(?!$)/g, match => {
  if (match.startsWith(',')) {
      return ', ';
  } else {
      return ' ' + match;
  }}).replace("ofthe "," of the ").replace("dela ", " de la ").replace("delos ", " de los ").replace("áD","á D").replace("eÁ","e Á")
  .replace("ed'","e d'").replace("leof ","le of ").replace("dde ","d de ").replace("iode ","io de ").replace("àde ","à de ")
  .replace("yof ","y of ").replace("Andrésy ","Andrés y")
  .replace("aand ","a and ").replace("iand ", "i and ").replace("tsand ","ts and ").replace("onand ","on and ").replace("reand ", "re and ")
  .replace("odel ","o del ").replace("adel ", "a del ").replace("ndel ","n del ").replace("zdel ","z del ").replace("falde ", "fal de ").replace("casdel ","cas del ")
  .replace("odosÓ", "o dos Ó")
  .replace("Grandedo ", "Grande do ").replace("Grandede ","Grande de ")
  .replace("Santiagode ","Santiago de ").replace("Joséde ","José de ").replace("Pedrode ","Pedro de ")
  .replace("andthe "," and the ")
  .replace("emunicipality", "e municipality").replace("Villede ", "Ville de ")
  .replace("Valledel ","Valle del ").replace("Valde ","Val de ").replace("Îlesdu ","Îles du ")
  .replace("sÉ","s É").replace("áO","á O").replace("N C Tof ","NCT of ").replace("N A","NA")
  .replace("Nortede ", "Norte de ")
  .replace("Pinardel ", "Pinar del ")
  .replace("Greeceand", "Greece and the Ionian")
  .replace("Vientiane", "Vientiane Province")
  .replace("Vientiane Province[prefecture]", "Vientiane Prefecture")
  .replace("Valduz", "Vaduz")
  .trim()

const parse0 = (country) => {
  const filepath = `temp/0/${country}.json`
  try{
    let geo0 = JSON.parse(readFileSync(filepath))
    let area = turf.area(geo0)
    
    for(let f of geo0.features){
      let feat = f.properties
      if (feat.GID_0==country){
        let gr = ""
        for(let g in groups){
          if(groups[g].includes(country)){
            gr = g
          }
        }
        dict0[feat.GID_0] = `${country}|${gr}|${formatStr(feat.COUNTRY)}|${Math.round(area/1e6)}`
      }
    }
  }catch(e){
    console.log(country," PARSE Error")
    console.log(e)
  }
}


const parse1 = (country) => {
  const filepath = `temp/1/${country}.json`
  try{
    if(!existsSync(filepath)){
      // console.log(`No L1 for ${country}`)
      return
    }
    let geo0 = JSON.parse(readFileSync(filepath))

    for(let f of geo0.features){
      let feat = f.properties
      let area = turf.area(f.geometry)
      dict1[feat.GID] = `${feat.GID}|${country}|${formatStr(feat.NAME)}|${Math.round(area/1e6)}`
    }
  }catch(e){
    console.log(country," PARSE Error")
    console.log(e)
  }
}


var mis = []
const mergePrint = (c)=>{

  // console.log(dict0[c])
  for (const key in dict1) {
    if (key.startsWith(c)) {
      console.log(dict1[key])
      mis.push(key)
  }}  
}

const wgPrint = ()=>{
  console.log("WWW||World|-1")
  console.log("XXX|WWW|Other|-1")
  console.log("EEE|WWW|Europe|-1")
  console.log("ABB|WWW|Asia|-1")
  console.log("FFF|WWW|Africa|-1")
  console.log("NNN|WWW|North America|-1")
  console.log("SRR|WWW|South America|-1")
  console.log("UUU|WWW|Oceana|-1")

}

async function run(){
  countries.forEach(c=>parse0(c))
  countries.forEach(c=>parse1(c))

  // wgPrint()
  countries.forEach(c=>mergePrint(c))

  for (const key in dict1) {
    if (!mis.includes(key)){
      console.log(dict1[key])
    }
  }
}


function fixSvg(svgPath) {
  const countryToRegion = {};
  for (const [region, countries] of Object.entries(groups)) {
    countries.forEach(country => countryToRegion[country] = region);
  }
  const absoluteInputPath = path.resolve(svgPath);
  if (!existsSync(absoluteInputPath)) {
    throw new Error(`Input file not found at: ${absoluteInputPath}`);
  }
  const svgContent = readFileSync(absoluteInputPath, 'utf8');
  const dom = new JSDOM(svgContent, { contentType: 'image/svg+xml' });
  const document = dom.window.document;
  const svgRoot = document.querySelector('svg');
    if (!svgRoot) {
      throw new Error("Invalid or empty SVG structure encountered.");
    }
    if (svgRoot.getAttribute('data-processed') === 'true') {
      console.log(`Skipping: File at "${svgPath}" has already been processed.`);
      return;
    }

  const elementGroups = Array.from(svgRoot.querySelectorAll('g'));
 elementGroups.forEach(group => {
    const currentId = group.getAttribute('id') || '';
    const baseIsoCode = currentId.replace(/\d+$/, '');
    const regionKey = countryToRegion[baseIsoCode] || 'XXXX';
    let regionGroup = svgRoot.querySelector(`g[id="${regionKey}"]`);
    if (!regionGroup) {
      regionGroup = document.createElementNS('http://w3.org', 'g');
      regionGroup.setAttribute('id', regionKey);
      svgRoot.appendChild(regionGroup);
    }
    regionGroup.appendChild(group);
  });
  svgRoot.setAttribute('data-processed', 'true');
  const absoluteOutputPath = path.resolve(svgPath);
  let cleanXmlString = svgRoot.outerHTML;
  cleanXmlString = cleanXmlString.replace(/[\r\n]+/g, '');
  cleanXmlString = cleanXmlString.replace(/xmlns="http:\/\/w3\.org"\s?/g, '');
  cleanXmlString = cleanXmlString.replace(/xmlns="http:\/\/www\.w3\.org\/2000\/svg"\s?/g, '');
  cleanXmlString = cleanXmlString.replace('<svg', '<svg xmlns="http://www.w3.org/2000/svg"');
  writeFileSync(absoluteOutputPath, cleanXmlString, 'utf8');
}

run()
fixSvg("./app/src/main/assets/loxim01.svg")
fixSvg("./app/src/main/assets/webmercator01.svg")
fixSvg("./app/src/main/assets/aeqd01.svg")