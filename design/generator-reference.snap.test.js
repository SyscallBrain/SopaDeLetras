var c=require("./generator-reference.js"), n=8, a=3*8+3, bad=0;
function eq(x,y,m){ if(JSON.stringify(x)!==JSON.stringify(y)){bad++;console.log("FAIL",m,JSON.stringify(x),JSON.stringify(y));} }
eq(c.snapLine(a,0.2,0.1,n),[a],"tiny");
eq(c.snapLine(a,3,0.4,n),[27,28,29,30],"right, wobbly");
eq(c.snapLine(a,-3.2,-0.5,n),[27,26,25,24],"left to edge");
eq(c.snapLine(a,0.3,2.6,n),[27,35,43,51],"down");
eq(c.snapLine(a,2,2.2,n),[27,36,45],"diag down-right");
eq(c.snapLine(a,-2,-1.8,n),[27,18,9],"diag up-left");
eq(c.snapLine(a,2,-2,n),[27,20,13],"diag up-right");
eq(c.snapLine(a,9,0,n),[27,28,29,30,31],"clamp at edge");
eq(c.snapLine(a,1.2,0.4,n),[27,28],"1 step");
console.log(bad?"snap FAILS "+bad:"snap tests ok");
eq(c.snapLine(a,6,-6,n),[27,20,13,6],"diag up-right to edge");
console.log(bad?"snap FAILS "+bad:"snap tests ok");
