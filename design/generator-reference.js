/* CORE-START */
var CFG = {
  6:  { words: 4,  dirs: [[0,1],[1,0]] },
  8:  { words: 6,  dirs: [[0,1],[1,0],[1,1]] },
  10: { words: 8,  dirs: [[0,1],[1,0],[1,1],[0,-1],[-1,0]] },
  12: { words: 10, dirs: [[0,1],[1,0],[1,1],[0,-1],[-1,0],[-1,-1],[1,-1],[-1,1]] }
};
var FILL = "AAAEEEIIOOOSSRRMNDTLCPUBG";
var CATS = [
  { id: "espaco", name: "Espaço", words: "SOL LUA LUZ CEU MARTE ASTRO NOITE SONHO NUVEM COMETA ORBITA AURORA SATURNO PLANETA GALAXIA FOGUETE NEBULOSA ECLIPSE CRATERA UNIVERSO ESTRELA JUPITER VENUS TELESCOPIO ESPACO ASTEROIDE GRAVIDADE MERCURIO NETUNO URANO PLUTAO POEIRA SONDA RADAR BRILHO VAZIO ESCURO ANEL SOLAR".split(" "),
    mys: { 4: ["NAVE", "Transporta astronautas"], 5: ["TERRA", "O nosso planeta"], 6: ["ZENITE", "Ponto mais alto no céu"], 7: ["CAPSULA", "Traz astronautas de volta"], 8: ["SATELITE", "Gira à volta de um planeta"] } },
  { id: "oceano", name: "Oceano", words: "MAR PEIXE POLVO CONCHA AREIA MARE ALGA NAVIO BARCO ANCORA FAROL GOLFINHO FOCA ORCA SARDINHA CARANGUEJO LULA MEDUSA RECIFE ILHA CAIS REDE VELA PORTO CORRENTE ESPUMA TEMPESTADE AGUA SAL ENGUIA RAIA".split(" "),
    mys: { 4: ["ONDA", "Vem e vai na praia"], 5: ["CORAL", "Forma recifes coloridos"], 6: ["BALEIA", "Maior animal do mar"], 7: ["TUBARAO", "Tem barbatana no topo"], 8: ["PESCADOR", "Vive da pesca"] } },
  { id: "comida", name: "Comida", words: "PAO QUEIJO ARROZ FEIJAO MASSA CARNE PEIXE OVO LEITE MANTEIGA FRUTA MACA PERA UVA LARANJA BANANA SALADA BOLO DOCE CAFE CHA SUMO BATATA CENOURA CEBOLA ALHO PIMENTA PASTEL".split(" "),
    mys: { 4: ["SOPA", "Prato quente de colher"], 5: ["PIZZA", "Nasceu em Nápoles"], 6: ["TOMATE", "Fruto vermelho de salada"], 7: ["LASANHA", "Massa em camadas"], 8: ["AZEITONA", "Dá azeite"] } },
  { id: "animais", name: "Animais", words: "GATO CAO CAVALO VACA PORCO OVELHA GALINHA PATO LOBO URSO RAPOSA TIGRE ZEBRA MACACO AGUIA CORUJA SAPO RATO TARTARUGA CROCODILO HIPOPOTAMO CAMELO CABRA BURRO PAPAGAIO FORMIGA ABELHA BORBOLETA".split(" "),
    mys: { 4: ["LEAO", "Rei da selva"], 5: ["COBRA", "Rasteja e silva"], 6: ["COELHO", "Orelhas compridas"], 7: ["PINGUIM", "Ave que não voa e vive no gelo"], 8: ["ELEFANTE", "Tem tromba"] } },
  { id: "paises", name: "Países", words: "PORTUGAL BRASIL ANGOLA INDIA JAPAO CHINA RUSSIA CANADA MEXICO EGITO GRECIA SUECIA NORUEGA TURQUIA IRAO IRAQUE NEPAL QUENIA MARROCOS TUNISIA ALEMANHA HOLANDA BELGICA POLONIA URUGUAI BOLIVIA".split(" "),
    mys: { 4: ["PERU", "Casa de Machu Picchu"], 5: ["CHILE", "País comprido na América do Sul"], 6: ["ITALIA", "Tem forma de bota"], 7: ["ESPANHA", "Vizinha de Portugal"], 8: ["COLOMBIA", "Famosa pelo café"] } }
];
var CATS_EN = [
  { id: "espaco", name: "Espaço", words: "SUN MOON STAR MARS VENUS EARTH ORBIT COMET GALAXY PLANET ROCKET ECLIPSE CRATER UNIVERSE NEBULA JUPITER SATURN URANUS NEPTUNE PLUTO MERCURY ASTEROID GRAVITY TELESCOPE COSMOS METEOR SATELLITE SOLAR LUNAR DUST RING PROBE RADAR LIGHT NIGHT DARK VOID ALIEN APOLLO SPACE".split(" "),
    mys: { 4: ["NOVA", "A star that suddenly shines bright"], 5: ["TITAN", "Largest moon of Saturn"], 6: ["ZENITH", "Highest point in the sky"], 7: ["CAPSULE", "Brings astronauts home"], 8: ["STARSHIP", "A ship that travels between stars"] } },
  { id: "oceano", name: "Oceano", words: "SEA FISH OCTOPUS SHELL SAND TIDE ALGAE SHIP BOAT ANCHOR LIGHTHOUSE DOLPHIN SEAL ORCA SARDINE CRAB SQUID JELLYFISH REEF ISLAND PIER NET SAIL HARBOR CURRENT FOAM STORM WATER SALT EEL RAY SHARK WHALE TURTLE".split(" "),
    mys: { 4: ["WAVE", "Rolls onto the beach"], 5: ["CORAL", "Builds colorful reefs"], 6: ["SAILOR", "Works on a ship"], 7: ["SEAWEED", "Green plant of the sea"], 8: ["SEAHORSE", "Tiny fish with a horse-like head"] } },
  { id: "comida", name: "Comida", words: "BREAD CHEESE RICE BEANS PASTA MEAT FISH EGG MILK BUTTER FRUIT APPLE PEAR GRAPE ORANGE BANANA SALAD CAKE SWEET COFFEE TEA JUICE POTATO CARROT ONION GARLIC PEPPER PANCAKE COOKIE HONEY SUGAR SALT".split(" "),
    mys: { 4: ["SOUP", "Hot dish eaten with a spoon"], 5: ["PIZZA", "Italian dish with cheese"], 6: ["TOMATO", "Red fruit used in salads"], 7: ["LASAGNA", "Layers of pasta"], 8: ["SANDWICH", "Filling between two slices of bread"] } },
  { id: "animais", name: "Animais", words: "CAT DOG HORSE COW PIG SHEEP CHICKEN DUCK WOLF BEAR FOX TIGER ZEBRA MONKEY EAGLE OWL FROG MOUSE TURTLE CROCODILE HIPPO CAMEL GOAT DONKEY PARROT ANT BEE BUTTERFLY GIRAFFE".split(" "),
    mys: { 4: ["LION", "King of the jungle"], 5: ["SNAKE", "Slithers and hisses"], 6: ["RABBIT", "Has long ears"], 7: ["PENGUIN", "Bird that swims and cannot fly"], 8: ["ELEPHANT", "Has a trunk"] } },
  { id: "paises", name: "Países", words: "PORTUGAL BRAZIL ANGOLA INDIA JAPAN CHINA RUSSIA CANADA MEXICO EGYPT GREECE SWEDEN NORWAY TURKEY IRAN IRAQ NEPAL KENYA MOROCCO TUNISIA GERMANY NETHERLANDS BELGIUM POLAND URUGUAY BOLIVIA ITALY SPAIN ENGLAND IRELAND".split(" "),
    mys: { 4: ["PERU", "Home of Machu Picchu"], 5: ["CHILE", "Long country in South America"], 6: ["FRANCE", "Country of the Eiffel Tower"], 7: ["AUSTRIA", "Land of Mozart"], 8: ["COLOMBIA", "Famous for its coffee"] } }
];
var LANGS = {
  pt: { label: "PT-PT", cats: CATS, fill: "AAAEEEIIOOOSSRRMNDTLCPUBG" },
  en: { label: "EN-US", cats: CATS_EN, fill: "EEEEAAARRIIOOTTNNSSLLCUDPMHGBF" }
};
function mulberry32(a) { return function () { a |= 0; a = a + 0x6D2B79F5 | 0; var t = Math.imul(a ^ a >>> 15, 1 | a); t = t + Math.imul(t ^ t >>> 7, 61 | t) ^ t; return ((t ^ t >>> 14) >>> 0) / 4294967296; }; }
function shuffle(a, rnd) { a = a.slice(); for (var i = a.length - 1; i > 0; i--) { var j = Math.floor(rnd() * (i + 1)); var t = a[i]; a[i] = a[j]; a[j] = t; } return a; }
function tryPlace(letters, n, w, dirs, rnd) {
  for (var tries = 0; tries < 300; tries++) {
    var d = dirs[Math.floor(rnd() * dirs.length)];
    var r = Math.floor(rnd() * n), c = Math.floor(rnd() * n);
    var er = r + d[0] * (w.length - 1), ec = c + d[1] * (w.length - 1);
    if (er < 0 || er >= n || ec < 0 || ec >= n) continue;
    var path = [], ok = true;
    for (var k = 0; k < w.length; k++) {
      var idx = (r + d[0] * k) * n + c + d[1] * k;
      if (letters[idx] && letters[idx] !== w[k]) { ok = false; break; }
      path.push(idx);
    }
    if (!ok) continue;
    path.forEach(function (idx, k) { letters[idx] = w[k]; });
    return path;
  }
  return null;
}
function fillRandom(letters, rnd) { for (var i = 0; i < letters.length; i++) if (!letters[i]) letters[i] = FILL[Math.floor(rnd() * FILL.length)]; }
function genClassic(n, bank, seed) {
  var rnd = mulberry32(seed), cfg = CFG[n], letters = new Array(n * n).fill(""), words = [];
  var cands = shuffle(bank.filter(function (w) { return w.length >= 3 && w.length <= n; }), rnd);
  for (var i = 0; i < cands.length && words.length < cfg.words; i++) {
    var p = tryPlace(letters, n, cands[i], cfg.dirs, rnd);
    if (p) words.push({ w: cands[i], cells: p });
  }
  fillRandom(letters, rnd);
  return { n: n, letters: letters, words: words, mystery: null };
}
function genMysteryOnce(n, cat, seed) {
  var rnd = mulberry32(seed), cfg = CFG[n], letters = new Array(n * n).fill(""), words = [];
  var cands = shuffle(cat.words.filter(function (w) { return w.length >= 3 && w.length <= n; }), rnd);
  var done = false;
  for (var i = 0; i < cands.length && !done; i++) {
    var snap = letters.slice();
    var p = tryPlace(letters, n, cands[i], cfg.dirs, rnd);
    if (!p) continue;
    var e = letters.filter(function (x) { return !x; }).length;
    if (e < 4) { for (var k = 0; k < letters.length; k++) letters[k] = snap[k]; continue; }
    words.push({ w: cands[i], cells: p });
    if (e <= 8) done = true;
  }
  if (!done) return null;
  var left = [];
  letters.forEach(function (x, idx) { if (!x) left.push(idx); });
  var m = cat.mys[left.length];
  if (!m) return null;
  left.forEach(function (idx, k) { letters[idx] = m[0][k]; });
  return { n: n, letters: letters, words: words, mystery: { word: m[0], clue: m[1], cells: left } };
}
function genMystery(n, cat, seed) {
  for (var a = 0; a < 400; a++) { var r = genMysteryOnce(n, cat, seed + a); if (r) return r; }
  if (n > 8) return genMystery(8, cat, seed);
  return genClassic(n, cat.words, seed);
}
var D = { 2: [[0,1],[1,0]], 3: [[0,1],[1,0],[1,1]], 5: [[0,1],[1,0],[1,1],[0,-1],[-1,0]], 8: [[0,1],[1,0],[1,1],[0,-1],[-1,0],[-1,-1],[1,-1],[-1,1]] };
var CAPS = { 6: 5, 8: 9, 10: 12, 12: 18 };
function diffParams(n, diff) {
  var cfg = CFG[n];
  if (diff === "easy") return { n: n, words: Math.max(3, cfg.words - 1), dirs: n >= 10 ? D[3] : D[2], decoy: 0 };
  if (diff === "hard") return { n: n, words: Math.min(CAPS[n], cfg.words + 2), dirs: D[8], decoy: 0.8 };
  return { n: n, words: cfg.words, dirs: cfg.dirs, decoy: 0.2 };
}
function countOcc(letters, n, w) {
  var c = 0;
  for (var r = 0; r < n; r++) for (var q = 0; q < n; q++) D[8].forEach(function (d) {
    for (var k = 0; k < w.length; k++) {
      var rr = r + d[0] * k, cc = q + d[1] * k;
      if (rr < 0 || rr >= n || cc < 0 || cc >= n || letters[rr * n + cc] !== w[k]) return;
    }
    c++;
  });
  return c;
}
function genOnce(pm, bank, seed, fill) {
  var n = pm.n, rnd = mulberry32(seed), letters = new Array(n * n).fill(""), words = [];
  var cands = shuffle(bank.filter(function (w) { return w.length >= 3 && w.length <= n; }), rnd);
  for (var i = 0; i < cands.length && words.length < pm.words; i++) {
    var p = tryPlace(letters, n, cands[i], pm.dirs, rnd);
    if (p) words.push({ w: cands[i], cells: p });
  }
  var nd = Math.round(pm.decoy * words.length * 0.5);
  for (var k = 0; k < nd; k++) {
    var src = words[Math.floor(rnd() * words.length)].w;
    if (src.length >= 5) tryPlace(letters, n, src.slice(0, src.length - 2), pm.dirs, rnd);
  }
  for (var j = 0; j < letters.length; j++) if (!letters[j]) letters[j] = fill[Math.floor(rnd() * fill.length)];
  return { n: n, letters: letters, words: words, mystery: null };
}
function generate(pm, bank, seed, fill) {
  var best = null;
  for (var a = 0; a < 8; a++) {
    var p = genOnce(pm, bank, seed + a * 977, fill);
    var ok = p.words.length >= pm.words && p.words.every(function (w) { return countOcc(p.letters, p.n, w.w) === 1; });
    if (ok) return p;
    if (!best || p.words.length > best.words.length) best = p;
  }
  return best;
}
var WORLDS = [
  { name: "Céu Noturno", cat: 0, n: 6, w: [3, 4], dirs: 2, decoy: 0 },
  { name: "Mar Profundo", cat: 1, n: 8, w: [4, 6], dirs: 3, decoy: 0 },
  { name: "Cozinha Aberta", cat: 2, n: 8, w: [6, 8], dirs: 5, decoy: 0.2 },
  { name: "Bicho Solto", cat: 3, n: 10, w: [6, 9], dirs: 3, decoy: 0.2 },
  { name: "Volta ao Mundo", cat: 4, n: 10, w: [8, 11], dirs: 5, decoy: 0.4 },
  { name: "Floresta Densa", cat: 5, n: 10, w: [10, 12], dirs: 8, decoy: 0.5 },
  { name: "Cidade Grande", cat: 6, n: 12, w: [8, 12], dirs: 5, decoy: 0.5 },
  { name: "Dia de Jogo", cat: 7, n: 12, w: [10, 14], dirs: 8, decoy: 0.6 },
  { name: "Casa Aberta", cat: 8, n: 12, w: [12, 16], dirs: 8, decoy: 0.8 },
  { name: "Corpo e Mente", cat: 9, n: 12, w: [14, 18], dirs: 8, decoy: 1 }
];
var LEVELS_PER_WORLD = 30;
function levelInfo(L) {
  var wi = Math.floor((L - 1) / LEVELS_PER_WORLD), l = (L - 1) % LEVELS_PER_WORLD, W = WORLDS[wi], num = l + 1;
  var n = W.n, words = Math.round(W.w[0] + (W.w[1] - W.w[0]) * l / (LEVELS_PER_WORLD - 1)), dk = W.dirs, kind = "normal";
  if (num === 10) kind = "timed";
  if (num === 20) { kind = "mystery"; n = Math.min(n, 10); }
  if (num === 30) { kind = "boss"; words = Math.min(CAPS[n], W.w[1] + 2); dk = 8; }
  return { L: L, world: wi, num: num, kind: kind, n: n, words: Math.min(words, CAPS[n]), dirs: D[dk], dirsCount: dk, decoy: W.decoy, cat: W.cat, seed: L * 7919 };
}
function levelParams(info) { return { n: info.n, words: info.words, dirs: info.dirs, decoy: info.decoy }; }
function lineCells(a, b, n) {
  var ar = Math.floor(a / n), ac = a % n, br = Math.floor(b / n), bc = b % n;
  var dr = br - ar, dc = bc - ac, len = Math.max(Math.abs(dr), Math.abs(dc));
  if (!(dr === 0 || dc === 0 || Math.abs(dr) === Math.abs(dc))) return [a];
  var out = [];
  for (var k = 0; k <= len; k++) out.push((ar + Math.sign(dr) * k) * n + ac + Math.sign(dc) * k);
  return out;
}
var SNAP_DIRS = [[0,1],[1,1],[1,0],[1,-1],[0,-1],[-1,-1],[-1,0],[-1,1]]; // (dr, dc), clockwise from "right"
// a: start cell index; vx, vy: pointer offset from the CENTER of the start cell, in cell pitches (x = columns, y = rows, y grows downwards)
function snapLine(a, vx, vy, n) {
  var dist = Math.sqrt(vx * vx + vy * vy);
  if (dist < 0.6) return [a];
  var sector = ((Math.round(Math.atan2(vy, vx) / (Math.PI / 4)) % 8) + 8) % 8;
  var d = SNAP_DIRS[sector], dr = d[0], dc = d[1];
  var k = Math.round((vx * dc + vy * dr) / (dr * dr + dc * dc));
  var r0 = Math.floor(a / n), c0 = a % n, out = [a];
  for (var s = 1; s <= k; s++) {
    var r = r0 + dr * s, c = c0 + dc * s;
    if (r < 0 || r >= n || c < 0 || c >= n) break;
    out.push(r * n + c);
  }
  return out;
}
/* CORE-END */
if (typeof module !== "undefined") module.exports = { CFG: CFG, CATS: CATS, genClassic: genClassic, genMystery: genMystery, genMysteryOnce: genMysteryOnce, lineCells: lineCells, snapLine: snapLine, CATS_EN: CATS_EN, LANGS: LANGS, D: D, CAPS: CAPS, diffParams: diffParams, generate: generate, countOcc: countOcc, WORLDS: WORLDS, levelInfo: levelInfo, levelParams: levelParams };
