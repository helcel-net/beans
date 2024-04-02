#!/bin/node

import {readFileSync, existsSync} from 'fs'
import area from '@turf/area'
import * as turf from '@turf/turf'


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
  "EEE":["ALB","AND","AUT","BLR","BEL","BIH","BGR","HRV","CYP","CZE","DNK","EST","FIN","FRA","DEU","GRC","HUN","ISL","IRL","ITA","KAZ","XKO","LVA","LIE","LTU","LUX","MLT","MDA","MCO","MNE","NLD","MKD","NOR","POL","PRT","ROU","RUS","SMR","SRB","SVK","SVN","ESP","SWE","CHE","UKR","GBR","VAT","XAD"],
  "ABB":["AFG","ARM","AZE","BHR","BGD","BTN","BRN","KHM","CHN","GEO","HKG","IND","IDN","IRN","IRQ","ISR","JPN","JOR","KWT","KGZ","LAO","LBN","MAC","MYS","MDV","MNG","MMR","NPL","PRK","OMN","PAK","PSE","PHL","QAT","SAU","SGP","KOR","LKA","SYR","TWN","TJK","THA","TLS","TUR","TKM","ARE","UZB","VNM","YEM","ZNC"],
  "FFF":["DZA","AGO","BDI","BEN","BWA","BFA","BDI","CPV","CMR","CAF","TCD","COM","COG","COD","CIV","DJI","EGY","GNQ","ERI","SWZ","ETH","GAB","GMB","GHA","GIN","GNB","KEN","LSO","LBR","LBY","MDG","MWI","MLI","MRT","MUS","MYT","MAR","MOZ","NAM","NER","NGA","COD","REU","RWA","STP","SEN","SYC","SLE","SOM","ZAF","SSD","SHN","SDN","TZA","TGO","TUN","UGA","COD","ZMB","ZWE","ESH"],
  "NNN":["ABW","AIA","ATG","BHS","BRB","BLZ","BMU","VGB","CAN","CYM","CRI","CUB","CUW","DMA","DOM","SLV","GRL","GRD","GLP","GTM","HTI","HND","JAM","MTQ","MEX","MSR","ANT","CUW","NIC","PAN","PRI","KNA","LCA","MAF","SPM","VCT","TTO","TCA","USA","XCL"],
  "SRR":["ARG","BOL","BRA","CHL","COL","ECU","FLK","GUF","GUY","PRY","PER","SUR","URY","VEN"],
  "UUU":["ASM","AUS","COK","FJI","PYF","GUM","KIR","MHL","FSM","NRU","NCL","NZL","NIU","NFK","MNP","PLW","PNG","PCN","SLB","TKL","TON","TUV","VUT","WLF"],
  "XXX":[
      "ATA", // Antarctica: not in any other region
      "ALA",// Åland Islands: an autonomous region of Finland, but not a member of the EU or UN
      "BES",// Bonaire, Sint Eustatius and Saba: special municipalities of the Netherlands in the Caribbean
      "BVT",// Bouvet Island: an uninhabited territory of Norway in the South Atlantic
      "IOT",// British Indian Ocean Territory: a British overseas territory in the Indian Ocean
      "CXR",// Christmas Island: an Australian external territory in the Indian Ocean
      "CCK",// Cocos (Keeling) Islands: an Australian external territory in the Indian Ocean
      "FRO",// Faroe Islands: an autonomous region of Denmark
      "ATF",// French Southern and Antarctic Lands: a territory of France located in the southern Indian Ocean
      "GIB",// Gibraltar: a British overseas territory located at the southern tip of the Iberian Peninsula
      "GGY",// Guernsey: a British Crown dependency in the English Channel
      "HMD",// Heard Island and McDonald Islands: an uninhabited Australian external territory in the southern Indian Ocean
      "IMN",// Isle of Man: a British Crown dependency located in the Irish Sea
      "JEY",// Jersey: a British Crown dependency located in the English Channel
      "BLM",// Saint Barthélemy: an overseas collectivity of France in the Caribbean
      "WSM",// Samoa: an independent island nation in the South Pacific
      "SXM",// Sint Maarten: a constituent country of the Kingdom of the Netherlands in the Caribbean
      "SGS",// South Georgia and the South Sandwich Islands: a British overseas territory in the southern Atlantic Ocean
      "SJM",// Svalbard and Jan Mayen: an archipelago administered by Norway
      "UMI",// United States Minor Outlying Islands: a collection of nine insular areas of the United States
      "VIR",// United States Virgin Islands: an unincorporated territory of the United States in the Caribbean
  ]
}

var dict0 = {}
var dict1 = {}

const formatStr = (str)=> str.replace(/(?<!\b\w)\B[A-Z]|,(?!$)/g, match => {
  if (match.startsWith(',')) {
      return ', ';
  } else {
      return ' ' + match;
  }})

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

run()
