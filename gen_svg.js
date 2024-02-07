#!/bin/node
#!/bin/node
import {readFileSync,createWriteStream,writeFileSync} from 'fs';
import {get as httpsGet} from 'https'
import {execSync} from 'child_process'


import convex from '@turf/convex'
import concave from '@turf/concave'
import simplify from '@turf/simplify'
import { polygon } from '@turf/helpers';
import {toMercator} from '@turf/projection'
import area from '@turf/area'

import geojson2svg from 'geojson2svg'

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

const parse0 = (country)=>{
  return new Promise((resolve, _reject) => {
    const filepath = `temp/${country}_0.json`
    const file = createWriteStream(filepath);
    httpsGet(url_0(country), r=>{
      r.pipe(file);
      file.on("finish", ()=>{
        file.close();
        try{
          var geo = JSON.parse(readFileSync(filepath))
          var geo_proj = toMercator(geo)
          var geo_simp = simplify(geo_proj,
            {tolerance: 1e4, highQuality: false, mutate:true})

          geo_simp.features = geo_simp.features.map(feat_e=>{
            feat_e.geometry.coordinates = feat_e.geometry.coordinates.filter(fc=>{
              try{
                if(fc.map(e=>e.length).reduce((a,b)=>Math.max(a,b))<=4) 
                  return false

                return area(polygon(fc))>=20_000_000**2
              }catch(e){
                console.log(e)
                return true
              }
            })
            return feat_e
          })

          var cc = convert(geo_simp)
          .map(scc => `<path d="` + scc + `"/>`)
          .join('')

          resolve(cc);
        }catch(e){
          console.log(country," Error")
          console.log(e)
          resolve('')
        }
      });
    })
  })
}

const convert = (geojson)=>{
  const converter = geojson2svg({
    viewportSize: {width:1200,height:1200},
    attributes: {},
    explode: true,
    precision: 5,
    output:'path'
  });
  return converter.convert(geojson);
}
async function run(){
  const cp = countries.map(c=>
    parse0(c).then(r=>{

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
