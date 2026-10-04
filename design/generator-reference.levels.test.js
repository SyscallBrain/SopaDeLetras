var c=require("./generator-reference.js"), bad=0, t0=Date.now(), cnt=0;
function chk(c_,m){ if(!c_){bad++; if(bad<15)console.log("FAIL",m);} }
// banks sanity
["pt","en"].forEach(function(lg){ c.LANGS[lg].cats.forEach(function(cat){
  var seen={}; cat.words.forEach(function(w){ chk(/^[A-Z]{3,12}$/.test(w),lg+" bad word "+w); chk(!seen[w],lg+" dup "+w); seen[w]=1; });
  [4,5,6,7,8].forEach(function(L){ var m=cat.mys[L]; chk(m&&m[0].length===L&&/^[A-Z]+$/.test(m[0]),lg+" mys "+cat.id+L); chk(cat.words.indexOf(m[0])===-1,lg+" mys in bank "+m[0]); });
}); });
["pt","en"].forEach(function(lg){
  var L=c.LANGS[lg];
  for(var lv=1; lv<=150; lv++){ var info=c.levelInfo(lv), cat=L.cats[info.cat]; cnt++;
    var p = info.kind==="mystery" ? c.genMystery(info.n,cat,info.seed) : c.generate(c.levelParams(info),cat.words,info.seed,L.fill);
    if(info.kind==="mystery") chk(p.mystery,lg+" lv"+lv+" mystery fell back");
    else { chk(p.words.length===info.words,lg+" lv"+lv+" words "+p.words.length+"/"+info.words+" n="+info.n);
      p.words.forEach(function(w){ chk(c.countOcc(p.letters,p.n,w.w)===1,lg+" lv"+lv+" dup occurrence "+w.w); }); }
  }
  ["easy","normal","hard"].forEach(function(df){ [6,8,10,12].forEach(function(n){ for(var s=1;s<=15;s++){ var pm=c.diffParams(n,df), cat=L.cats[s%5]; var p=c.generate(pm,cat.words,s*131,L.fill); cnt++;
    chk(p.words.length>=pm.words-(df==="hard"?2:0),lg+" "+df+" "+n+" short "+p.words.length+"/"+pm.words); } }); });
});
console.log("generated",cnt,"puzzles, fails",bad,"avg ms",((Date.now()-t0)/cnt).toFixed(1));
// level table
for(var w=0;w<10;w++){ var a=c.levelInfo(w*30+1), b=c.levelInfo(w*30+29), z=c.levelInfo(w*30+30); console.log("W"+(w+1),c.WORLDS[w].name,a.n+"x"+a.n,"words",a.words+"->"+b.words,"boss",z.words,"dirs",a.dirsCount); }
