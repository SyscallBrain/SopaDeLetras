var c = require("./generator-reference.js");
var fails = 0;
[6, 8, 10, 12].forEach(function (n) {
  c.CATS.forEach(function (cat) {
    for (var s = 1; s <= 40; s++) {
      var p = c.genClassic(n, cat.words, s * 7919);
      if (p.words.length !== c.CFG[n].words) { fails++; console.log("classic short", n, cat.id, s, p.words.length); }
      p.words.forEach(function (w) { if (w.cells.map(function (i) { return p.letters[i]; }).join("") !== w.w) { fails++; console.log("bad word"); } });
    }
  });
});
console.log("classic fails", fails);
[6, 8, 10, 12].forEach(function (n) {
  var ok = 0, tot = 0, firstTry = 0;
  c.CATS.forEach(function (cat) {
    for (var s = 1; s <= 40; s++) {
      tot++;
      if (c.genMysteryOnce(n, cat, s * 104729)) firstTry++;
      var p = c.genMystery(n, cat, s * 104729);
      if (p.mystery) {
        ok++;
        var read = p.mystery.cells.map(function (i) { return p.letters[i]; }).join("");
        var covered = {}; p.words.forEach(function (w) { w.cells.forEach(function (i) { covered[i] = 1; }); });
        var left = p.letters.map(function (_, i) { return i; }).filter(function (i) { return !covered[i]; });
        if (read !== p.mystery.word || left.join() !== p.mystery.cells.join()) { fails++; console.log("mystery mismatch", n, cat.id, s); }
      }
    }
  });
  console.log("mystery n=" + n, "first-try", firstTry + "/" + tot, "final ok", ok + "/" + tot);
});
console.log("total fails", fails);
