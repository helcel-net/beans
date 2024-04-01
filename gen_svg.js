#!/bin/node

import {readFileSync,createWriteStream,writeFileSync, existsSync} from 'fs';
import {get as httpsGet} from 'https';

import mapshaper from  'mapshaper';
import {GeoJSON2SVG} from 'geojson2svg';

const LOCAL_SVG_PATH = "./app/src/main/assets/"

const GADM_VERSION="4.1"
const GADM_BASEPATH="https://geodata.ucdavis.edu/gadm"

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


const url_0 = (country) => `${GADM_BASEPATH}/gadm${GADM_VERSION}/json/gadm${GADM_VERSION.replace(".","")}_${country}_0.json`;
const url_1 = (country) => `${GADM_BASEPATH}/gadm${GADM_VERSION}/json/gadm${GADM_VERSION.replace(".","")}_${country}_1.json.zip`;

const dl0 = (country) => new Promise((resolve,_reject) => {
  const filepath = `temp/${country}_0.json`
  if (existsSync(filepath)){
    return resolve(filepath)
  }
  try{
    httpsGet(url_0(country), r=>{
      const file = createWriteStream(filepath);
      r.pipe(file);
      file.on("finish", ()=>{
        file.close();
        resolve(filepath)
      })
    })
  }catch(e){
    console.log("DL Error:",country)
    console.log(e)
    resolve("")
  }
})

const parse0 = (country) => new Promise((resolve, _reject) => {
  const filepath = `temp/${country}_0.json`
  try{
    let geo0 = JSON.parse(readFileSync(filepath))
      simplify(geo0)
      .then(geo1=>{
        resolve(toSVG(geo1).join(''))
      })
    }catch(e){
    console.log(country," PARSE Error")
    console.log(e)
    resolve('')
  }
});

const toSVG = (geojson)=> new GeoJSON2SVG({
    viewportSize: {width:720,height:720},
    attributes: {},
    mapExtent: {left: -180, bottom: -90, right: 180, top: 90},
    precision: 0,
    output:'svg'
  }).convert(geojson)

const simplify = (geo0) => mapshaper.applyCommands(`-i data.json -simplify 5% visvalingam weighted -o data.json rfc7946 -o data.svg `, { 'data.json': geo0}).then(res=>{
  const geo1 = JSON.parse(res['data.json'].toString())
  const svg = res['data.svg'].toString()
  console.log(svg)
  geo1.features = geo1.features.filter(e=>e.geometry != null)
  return geo1
})

async function run(){
  const cp = countries.map(c=>
    dl0(c).then(_=> parse0(c)).then(r=>{
      writeFileSync(LOCAL_SVG_PATH+c+"_0.psvg",r)
      return r
    })
  )
  const cpp = await Promise.all(cp)
  // console.log(cpp)
  var svgStr = `<svg id="map" xmlns="http://www.w3.org/2000/svg" width="1200" height="1200" x="0" y="0" >`+
  cpp.join('')
  + `</svg>`;
  writeFileSync('./temp/example.svg',svgStr);
  cpp.forEach(e=>{
    writeFileSync
  })
}

run()
