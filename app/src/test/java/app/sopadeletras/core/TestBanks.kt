package app.sopadeletras.core

// Test fixtures copied from design/generator-reference.js (5 PT categories).
// Full 10-category banks per language arrive in M8; until then tests that
// need a real bank use these, and campaign levels beyond 150 reuse them
// cyclically.
internal val PT_ESPACO = listOf(
    "SOL", "LUA", "LUZ", "CEU", "MARTE", "ASTRO", "NOITE", "SONHO", "NUVEM",
    "COMETA", "ORBITA", "AURORA", "SATURNO", "PLANETA", "GALAXIA", "FOGUETE",
    "NEBULOSA", "ECLIPSE", "CRATERA", "UNIVERSO", "ESTRELA", "JUPITER", "VENUS",
    "TELESCOPIO", "ESPACO", "ASTEROIDE", "GRAVIDADE", "MERCURIO", "NETUNO",
    "URANO", "PLUTAO", "POEIRA", "SONDA", "RADAR", "BRILHO", "VAZIO", "ESCURO",
    "ANEL", "SOLAR",
)
internal val PT_OCEANO = listOf(
    "MAR", "PEIXE", "POLVO", "CONCHA", "AREIA", "MARE", "ALGA", "NAVIO",
    "BARCO", "ANCORA", "FAROL", "GOLFINHO", "FOCA", "ORCA", "SARDINHA",
    "CARANGUEJO", "LULA", "MEDUSA", "RECIFE", "ILHA", "CAIS", "REDE", "VELA",
    "PORTO", "CORRENTE", "ESPUMA", "TEMPESTADE", "AGUA", "SAL", "ENGUIA", "RAIA",
)
internal val PT_COMIDA = listOf(
    "PAO", "QUEIJO", "ARROZ", "FEIJAO", "MASSA", "CARNE", "PEIXE", "OVO",
    "LEITE", "MANTEIGA", "FRUTA", "MACA", "PERA", "UVA", "LARANJA", "BANANA",
    "SALADA", "BOLO", "DOCE", "CAFE", "CHA", "SUMO", "BATATA", "CENOURA",
    "CEBOLA", "ALHO", "PIMENTA", "PASTEL",
)
internal val PT_ANIMAIS = listOf(
    "GATO", "CAO", "CAVALO", "VACA", "PORCO", "OVELHA", "GALINHA", "PATO",
    "LOBO", "URSO", "RAPOSA", "TIGRE", "ZEBRA", "MACACO", "AGUIA", "CORUJA",
    "SAPO", "RATO", "TARTARUGA", "CROCODILO", "HIPOPOTAMO", "CAMELO", "CABRA",
    "BURRO", "PAPAGAIO", "FORMIGA", "ABELHA", "BORBOLETA",
)
internal val PT_PAISES = listOf(
    "PORTUGAL", "BRASIL", "ANGOLA", "INDIA", "JAPAO", "CHINA", "RUSSIA",
    "CANADA", "MEXICO", "EGITO", "GRECIA", "SUECIA", "NORUEGA", "TURQUIA",
    "IRAO", "IRAQUE", "NEPAL", "QUENIA", "MARROCOS", "TUNISIA", "ALEMANHA",
    "HOLANDA", "BELGICA", "POLONIA", "URUGUAI", "BOLIVIA",
)
internal val PT_BANKS = listOf(PT_ESPACO, PT_OCEANO, PT_COMIDA, PT_ANIMAIS, PT_PAISES)

internal val PT_MYSTERIES: Map<Int, MysteryEntry> = mapOf(
    4 to MysteryEntry("NAVE", "Transporta astronautas"),
    5 to MysteryEntry("TERRA", "O nosso planeta"),
    6 to MysteryEntry("ZENITE", "Ponto mais alto no ceu"),
    7 to MysteryEntry("CAPSULA", "Traz astronautas de volta"),
    8 to MysteryEntry("SATELITE", "Gira a volta de um planeta"),
)

internal const val PT_FILL_REF = "AAAEEEIIOOOSSRRMNDTLCPUBG"
internal const val EN_FILL_REF = "EEEEAAARRIIOOTTNNSSLLCUDPMHGBF"
