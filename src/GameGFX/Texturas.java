package GameGFX;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Sistema de texturas con soporte multi-nivel
 * Patrón SINGLETON + FACTORY para gestión de recursos
 * 
 * @author LENOVO
 */
public class Texturas {
    private static Texturas instancia; // Singleton
    
    private final String folder = "/Imagenes";
    
    // Contadores de sprites
    private final int mario_L_count = 21;
    private final int mario_S_count = 14;
    private final int mario_muerte_count = 6;
    private final int mario_martillo_count = 6;
    private final int barril_count = 8;
    private final int diegokong_count = 8;
    private final int bloque_count = 8;
    private final int princesa_count = 8;
    private final int fuego_count = 4;
    private final int llama_count = 4;
    private final int martillo_count = 4;
    private final int items_count = 8;
    private final int victoria_count = 3; // 🆕 NUEVO
    
    private CargadorImagenes cargar;
    
    // HOJAS DE SPRITES GLOBALES
    private BufferedImage player_sheet, enemy_sheet_, barril_sheet;
    private BufferedImage diegokong_sheet, princesaSheet, fuego_sheet, llama_sheet;
    private BufferedImage items_sheet;
    private BufferedImage victoria_sheet; // 🆕 NUEVO
    
    // HOJAS DE SPRITES POR NIVEL (Patrón STRATEGY)
    private HashMap<Integer, BufferedImage> bloquesSheetsPorNivel;
    private HashMap<Integer, HashMap<Integer, BufferedImage>> tilesSpritesPorNivel;
    
    // ARRAYS DE SPRITES GLOBALES
    private BufferedImage[] mario_l, mario_s, tile1, tile2, tile3, tile4, mario_martillo;
    private BufferedImage[] barril_sprites, diegokong_sprites, bloque_sprites;
    private BufferedImage[] mario_muerte;
    private BufferedImage[] princesaSprites, fuego_sprites, llama_sprites;
    private BufferedImage[] martillo_sprites, paraguas_sprites, bolso_sprites, sombrero_sprites;
    
    // 🆕 SPRITES DE VICTORIA
    private BufferedImage spriteCorazon;
    private BufferedImage spriteCorazonRoto;
    private BufferedImage[] spritesDKAgarra;
    
    // NIVEL ACTUAL
    private int nivelActual = 1;
    
    /**
     * Constructor privado (Singleton)
     */
    public Texturas() {
        inicializarArrays();
        cargar = new CargadorImagenes();
        bloquesSheetsPorNivel = new HashMap<>();
        tilesSpritesPorNivel = new HashMap<>();
        
        try {
            cargarSpritesGlobales();
            cargarSpritesVictoria(); // 🆕 NUEVO
            cargarTodosLosNiveles();
        } catch (Exception e) {
            System.err.println("[ERROR CRÍTICO] Fallo al cargar texturas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Singleton instance getter
     */
    public static Texturas getInstance() {
        if (instancia == null) {
            instancia = new Texturas();
        }
        return instancia;
    }
    
    /**
     * Inicializa todos los arrays
     */
    private void inicializarArrays() {
        mario_l = new BufferedImage[mario_L_count];
        mario_s = new BufferedImage[mario_S_count];
        mario_martillo = new BufferedImage[mario_martillo_count];
        tile1 = new BufferedImage[61]; // Aumentado para soportar más tiles
        tile2 = new BufferedImage[61];
        tile3 = new BufferedImage[61];
        tile4 = new BufferedImage[61];
        barril_sprites = new BufferedImage[barril_count];
        diegokong_sprites = new BufferedImage[diegokong_count];
        princesaSprites = new BufferedImage[princesa_count];
        fuego_sprites = new BufferedImage[fuego_count];
        llama_sprites = new BufferedImage[llama_count];
        mario_muerte = new BufferedImage[mario_muerte_count];
        martillo_sprites = new BufferedImage[1];
        paraguas_sprites = new BufferedImage[1];
        bolso_sprites = new BufferedImage[1];
        sombrero_sprites = new BufferedImage[1];
        spritesDKAgarra = new BufferedImage[victoria_count]; // 🆕 NUEVO
    }
    
    /**
     * Carga sprites que son comunes a todos los niveles
     */
    private void cargarSpritesGlobales() {
        System.out.println("[TEXTURAS] Cargando sprites globales...");
        
        player_sheet = cargar.loadImage(folder + "/testt.png");
        barril_sheet = cargar.loadImage(folder + "/testt.png");
        diegokong_sheet = cargar.loadImage(folder + "/testt.png");
        princesaSheet = cargar.loadImage(folder + "/testt.png");
        fuego_sheet = cargar.loadImage(folder + "/testt.png");
        llama_sheet = cargar.loadImage(folder + "/testt.png");
        items_sheet = cargar.loadImage(folder + "/testt.png");
        victoria_sheet = cargar.loadImage(folder + "/testt.png"); // 🆕 NUEVO
        
        getPlayerTexturas();
        getBarrilTexturas();
        getItemsTexturas();
        getPlayerMartilloTexturas();
        getDiegoKongTexturas();
        getPrincesaTexturas();
        getFuegoTexturas();
        getLlamaTexturas();
        getPlayerMuerteTexturas();
    }
    
    /**
     * 🆕 NUEVO: Carga sprites de animación de victoria
     */
    private void cargarSpritesVictoria() {
        System.out.println("[TEXTURAS] Cargando sprites de victoria...");
        
        try {
            // ==================== CORAZÓN (1 sprite estático) ====================
            int x_corazon = 109;
            int y_corazon = 157;
            int w_corazon = 16;
            int h_corazon = 16;
            
            spriteCorazon = victoria_sheet.getSubimage(x_corazon, y_corazon, w_corazon, h_corazon);
            
            // ==================== CORAZÓN ROTO (1 sprite estático) ====================
            int x_corazonRoto = 127;
            int y_corazonRoto = 157;
            int w_corazonRoto = 16;
            int h_corazonRoto = 16;
            
            spriteCorazonRoto = victoria_sheet.getSubimage(x_corazonRoto, y_corazonRoto, w_corazonRoto, h_corazonRoto);
            
            // ==================== DK AGARRA PRINCESA (3 frames) ====================
            // Estos están en la fila 2 de Diego Kong (frames específicos de agarrar)
            int x_dk_base = 1;
            int y_dk_fila2 = 368;
            int w_dk = 48;
            int h_dk = 32;
            
            for (int i = 0; i < victoria_count; i++) {
                spritesDKAgarra[i] = diegokong_sheet.getSubimage(
                    x_dk_base + i * (w_dk + 2),
                    y_dk_fila2,
                    w_dk,
                    h_dk
                );
            }
            
            System.out.println("[TEXTURAS] ✅ Sprites de victoria cargados:");
            System.out.println("  - Corazón:       " + (spriteCorazon != null ? "OK" : "FAIL"));
            System.out.println("  - Corazón Roto:  " + (spriteCorazonRoto != null ? "OK" : "FAIL"));
            System.out.println("  - DK Agarra:     " + spritesDKAgarra.length + " frames");
            
        } catch (Exception e) {
            System.err.println("[ERROR] ❌ Fallo al cargar sprites de victoria: " + e.getMessage());
            e.printStackTrace();
            
            // Crear placeholders si falla
            spriteCorazon = crearPlaceholderCorazon(16, 16, false);
            spriteCorazonRoto = crearPlaceholderCorazon(16, 16, true);
            
            for (int i = 0; i < victoria_count; i++) {
                spritesDKAgarra[i] = crearPlaceholder(48, 32, java.awt.Color.ORANGE);
            }
        }
    }
    
    /**
     * 🆕 Crea placeholder de corazón
     */
    private BufferedImage crearPlaceholderCorazon(int width, int height, boolean roto) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        
        // Forma de corazón
        g.setColor(roto ? new java.awt.Color(139, 0, 0) : java.awt.Color.RED);
        
        // Dos círculos arriba
        g.fillOval(2, 3, 5, 5);
        g.fillOval(9, 3, 5, 5);
        
        // Triángulo abajo
        int[] xPoints = {8, 2, 14};
        int[] yPoints = {13, 7, 7};
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Si está roto, agregar grieta
        if (roto) {
            g.setColor(java.awt.Color.BLACK);
            g.setStroke(new java.awt.BasicStroke(2));
            g.drawLine(8, 2, 8, 13);
        }
        
        g.dispose();
        return img;
    }
    
    /**
     * Carga todas las hojas de bloques de todos los niveles
     */
    private void cargarTodosLosNiveles() {
        System.out.println("[TEXTURAS] Cargando texturas de todos los niveles...");
        
        // Nivel 1: bloques2.png
        cargarNivel(1, "/bloques2.png");
        
        // Nivel 2: bloques3.png
        cargarNivel(2, "/bloques3.png");
        
        // Por defecto, cargar nivel 1
        cambiarNivel(1);
    }
    
    /**
     * Carga sprites de bloques de un nivel específico
     * Patrón FACTORY
     */
    private void cargarNivel(int numeroNivel, String nombreArchivo) {
        try {
            System.out.println("[TEXTURAS] Cargando nivel " + numeroNivel + ": " + nombreArchivo);
            
            BufferedImage sheet = cargar.loadImage(folder + nombreArchivo);
            
            if (sheet == null) {
                System.err.println("[ERROR] No se pudo cargar: " + nombreArchivo);
                return;
            }
            
            // Guardar sheet del nivel
            bloquesSheetsPorNivel.put(numeroNivel, sheet);
            
            // Extraer sprites de este nivel
            HashMap<Integer, BufferedImage> spritesNivel = new HashMap<>();
            extraerSpritesNivel(sheet, spritesNivel);
            
            // Guardar sprites del nivel
            tilesSpritesPorNivel.put(numeroNivel, spritesNivel);
            
            System.out.println("[TEXTURAS] ✅ Nivel " + numeroNivel + " cargado: " + 
                             spritesNivel.size() + " tiles");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar nivel " + numeroNivel + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extrae todos los sprites de una hoja de bloques
     */
    private void extraerSpritesNivel(BufferedImage sheet, HashMap<Integer, BufferedImage> sprites) {
        final int x_off = 0;
        final int y_off = 0;
        final int tileWidth = 8;
        final int tileHeight = 8;
        final int firstgid = 1;
        final int NUM_FILAS = 5;
        
        int currentTileID = firstgid;
        
        for (int fila = 0; fila < NUM_FILAS; fila++) {
            int y = y_off + fila * tileHeight;
            
            if (y + tileHeight > sheet.getHeight()) {
                break;
            }
            
            for (int x = x_off; x + tileWidth <= sheet.getWidth(); x += tileWidth) {
                BufferedImage sprite = sheet.getSubimage(x, y, tileWidth, tileHeight);
                sprites.put(currentTileID, sprite);
                currentTileID++;
            }
        }
    }
    
    /**
     * 🆕 NUEVO: Cambia al nivel especificado
     * Patrón STRATEGY - cambia la estrategia de texturas según el nivel
     */
    public void cambiarNivel(int numeroNivel) {
        if (!tilesSpritesPorNivel.containsKey(numeroNivel)) {
            System.err.println("[ERROR] Nivel " + numeroNivel + " no existe en texturas");
            return;
        }
        
        this.nivelActual = numeroNivel;
        System.out.println("[TEXTURAS] ✅ Cambiado a nivel " + numeroNivel);
    }
    
    /**
     * Obtiene sprite de bloque según el nivel actual
     * Patrón FACTORY METHOD
     */
    public BufferedImage getSpritePorID(int tileID) {
        HashMap<Integer, BufferedImage> spritesActuales = tilesSpritesPorNivel.get(nivelActual);
        
        if (spritesActuales == null) {
            System.err.println("[ADVERTENCIA] No hay sprites para nivel " + nivelActual);
            return null;
        }
        
        BufferedImage sprite = spritesActuales.get(tileID);
        
        if (sprite == null && tileID > 0) {
            System.err.println("[ADVERTENCIA] No se encontró sprite para tileID: " + tileID + 
                             " en nivel " + nivelActual);
        }
        
        return sprite;
    }
    
    // ==================== MÉTODOS EXISTENTES (sin cambios) ====================
    
    private void getPlayerTexturas() {
        int x_off = 1;
        int y_off = 1;
        int width = 16;
        int height = 16;
        
        for (int i = 0; i < mario_S_count; i++) {
            mario_s[i] = player_sheet.getSubimage(x_off + i * (width + 2), y_off, width, height);
        }
    }
    
    private void getPlayerMartilloTexturas() {
        int x_off = 1;
        int y_off = 73;
        int width = 32;
        int height = 32;
        
        try {
            for (int i = 0; i < mario_martillo_count; i++) {
                mario_martillo[i] = player_sheet.getSubimage(
                    x_off + i * (width + 2),
                    y_off,
                    width,
                    height
                );
            }
            System.out.println("[TEXTURAS] Sprites de Mario con martillo cargados: " + mario_martillo_count);
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar sprites de Mario con martillo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void getPlayerMuerteTexturas() {
        int x_off = 1;
        int y_off = 37;
        int width = 16;
        int height = 16;
        
        for (int i = 0; i < mario_muerte_count; i++) {
            mario_muerte[i] = player_sheet.getSubimage(x_off + i * (width + 2), y_off, width, height);
        }
    }
    
    private void getItemsTexturas() {
        try {
            // MARTILLO
            int x_martillo = 1;
            int y_martillo = 55;
            int w_martillo = 16;
            int h_martillo = 16;
            
            martillo_sprites[0] = items_sheet.getSubimage(x_martillo, y_martillo, w_martillo, h_martillo);
            
            // ITEMS DE BONIFICACIÓN
            int x_items = 145;
            int y_items = 157;
            int w_item = 16;
            int h_item = 16;
            int spacing = 2;
            
            paraguas_sprites[0] = items_sheet.getSubimage(x_items, y_items, w_item, h_item);
            
            int x_bolso = x_items + (w_item + spacing) * 2;
            bolso_sprites[0] = items_sheet.getSubimage(x_bolso, y_items, w_item, h_item);
            
            int x_sombrero = x_items + (w_item + spacing) * 4;
            sombrero_sprites[0] = items_sheet.getSubimage(x_sombrero, y_items, w_item, h_item);
            
            System.out.println("[TEXTURAS] ✅ Sprites de items cargados correctamente");
            
        } catch (Exception e) {
            System.err.println("[ERROR] ❌ Fallo al cargar sprites de items: " + e.getMessage());
            e.printStackTrace();
            
            martillo_sprites[0] = crearPlaceholder(16, 16, new java.awt.Color(128, 128, 128));
            paraguas_sprites[0] = crearPlaceholder(16, 16, new java.awt.Color(255, 0, 0));
            bolso_sprites[0] = crearPlaceholder(16, 16, new java.awt.Color(255, 192, 203));
            sombrero_sprites[0] = crearPlaceholder(16, 16, java.awt.Color.BLACK);
        }
    }
    
    private BufferedImage crearPlaceholder(int width, int height, java.awt.Color color) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();
        g.setColor(color);
        g.fillRect(0, 0, width, height);
        g.setColor(java.awt.Color.WHITE);
        g.drawRect(0, 0, width - 1, height - 1);
        g.dispose();
        return img;
    }
    
    private void getBarrilTexturas() {
        int x_off = 1;
        int y_off = 229;
        int width = 16;
        int height = 16;
        
        for (int i = 0; i < barril_count; i++) {
            barril_sprites[i] = barril_sheet.getSubimage(
                x_off + i * (width + 2),
                y_off,
                width,
                height
            );
        }
    }
    
    private void getDiegoKongTexturas() {
        int width = 48;
        int height = 32;
        int spritesPrimeraFila = 4;
        
        try {
            int x_off_fila1 = 1;
            int y_off_fila1 = 258;
            for (int i = 0; i < spritesPrimeraFila && i < diegokong_count; i++) {
                diegokong_sprites[i] = diegokong_sheet.getSubimage(
                    x_off_fila1 + i * (width + 2),
                    y_off_fila1,
                    width,
                    height
                );
            }
            
            int x_off_fila2 = 1;
            int y_off_fila2 = 292;
            for (int i = spritesPrimeraFila; i < diegokong_count; i++) {
                int spriteIndexEnFila = i - spritesPrimeraFila;
                diegokong_sprites[i] = diegokong_sheet.getSubimage(
                    x_off_fila2 + spriteIndexEnFila * (width + 2),
                    y_off_fila2,
                    width,
                    height
                );
            }
            
            System.out.println("[TEXTURAS] Sprites de Diego Kong cargados: " + diegokong_count);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar sprites de Diego Kong: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void getPrincesaTexturas() {
        int x_off = 1;
        int y_off = 141;
        int width = 16;
        int height = 32;
        
        for (int i = 0; i < princesa_count; i++) {
            princesaSprites[i] = princesaSheet.getSubimage(
                x_off + i * (width + 2), y_off, width, height
            );
        }
    }
    
    private void getFuegoTexturas() {
        int x_off = 1;
        int y_off = 193;
        int width = 16;
        int height = 16;
        
        try {
            for (int i = 0; i < fuego_count; i++) {
                fuego_sprites[i] = fuego_sheet.getSubimage(
                    x_off + i * (width + 2),
                    y_off,
                    width,
                    height
                );
            }
            
            System.out.println("[TEXTURAS] Sprites de Fuego cargados: " + fuego_count);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar sprites de Fuego: " + e.getMessage());
            e.printStackTrace();
            
            for (int i = 0; i < fuego_count; i++) {
                fuego_sprites[i] = crearFuegoPlaceholder(width, height, i);
            }
        }
    }
    
    private BufferedImage crearFuegoPlaceholder(int width, int height, int frame) {
        BufferedImage sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = sprite.createGraphics();
        
        java.awt.Color[] colores = {
            new java.awt.Color(255, 69, 0),
            new java.awt.Color(255, 140, 0),
            new java.awt.Color(255, 165, 0),
            new java.awt.Color(255, 215, 0)
        };
        
        g.setColor(colores[frame % 4]);
        
        int[] xPoints = {width / 2, width / 4, 0, width / 4, width / 2, 3 * width / 4, width, 3 * width / 4};
        int[] yPoints = {0, height / 4, height / 2, 3 * height / 4, height, 3 * height / 4, height / 2, height / 4};
        g.fillPolygon(xPoints, yPoints, 8);
        
        g.setColor(java.awt.Color.YELLOW);
        g.fillOval(width / 4, height / 3, width / 2, height / 2);
        
        g.dispose();
        return sprite;
    }
    
    private void getLlamaTexturas() {
        int x_off = 163;
        int y_off = 193;
        int width = 16;
        int height = 16;
        
        try {
            for (int i = 0; i < llama_count; i++) {
                llama_sprites[i] = llama_sheet.getSubimage(
                    x_off + i * (width + 2),
                    y_off,
                    width,
                    height
                );
            }
            
            System.out.println("[TEXTURAS] Sprites de Llama cargados: " + llama_count);
            
        } catch (Exception e) {
            System.err.println("[ERROR] Fallo al cargar sprites de Llama: " + e.getMessage());
            e.printStackTrace();
            
            for (int i = 0; i < llama_count; i++) {
                llama_sprites[i] = crearLlamaPlaceholder(width, height, i);
            }
        }
    }
    
    private BufferedImage crearLlamaPlaceholder(int width, int height, int frame) {
        BufferedImage sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = sprite.createGraphics();
        
        java.awt.Color colorBase = new java.awt.Color(255, 69, 0);
        java.awt.Color colorMedio = new java.awt.Color(255, 140, 0);
        java.awt.Color colorPunta = new java.awt.Color(255, 215, 0);
        
        int alturaVar = (frame % 2 == 0) ? 2 : -2;
        
        g.setColor(colorBase);
        int[] xBase = {width / 2, width / 4, 0, width / 4, width / 2, 3 * width / 4, width, 3 * width / 4};
        int[] yBase = {height + alturaVar, height - 2, height - 4, height - 6, height - 8, height - 6, height - 4, height - 2};
        g.fillPolygon(xBase, yBase, 8);
        
        g.setColor(colorMedio);
        int[] xMedio = {width / 2, width / 3, width / 6, width / 3, width / 2, 2 * width / 3, 5 * width / 6, 2 * width / 3};
        int[] yMedio = {height - 8 + alturaVar, height - 10, height - 12, height - 14, height - 16, height - 14, height - 12, height - 10};
        g.fillPolygon(xMedio, yMedio, 8);
        
        g.setColor(colorPunta);
        int[] xPunta = {width / 2, width / 3, width / 3, width / 2, 2 * width / 3, 2 * width / 3};
        int[] yPunta = {0 + alturaVar, 4, height / 3, height / 3 - 4, height / 3, 4};
        g.fillPolygon(xPunta, yPunta, 6);
        
        g.dispose();
        return sprite;
    }
    
    // ==================== GETTERS ====================
    
    public BufferedImage[] getMarioL() {
        return mario_l;
    }
    
    public BufferedImage[] getMarioS() {
        return mario_s;
    }
    
    public BufferedImage[] getMarioMartillo() {
        return mario_martillo;
    }
    
    public BufferedImage[] getMarioMuerte() {
        return mario_muerte;
    }
    
    public BufferedImage[] getTile1() {
        return tile1;
    }
    
    public BufferedImage[] getTile2() {
        return tile2;
    }
    
    public BufferedImage[] getTile3() {
        return tile3;
    }
    
    public BufferedImage[] getTile4() {
        return tile4;
    }
    
    public BufferedImage[] getBarrilSprites() {
        return barril_sprites;
    }
    
    public BufferedImage[] getDiegoKongSprites() {
        return diegokong_sprites;
    }
    
    public BufferedImage[] getPrincesaSprites() {
        return princesaSprites;
    }
    
    public BufferedImage[] getFuegoSprites() {
        return fuego_sprites;
    }
    
    public BufferedImage[] getLlamaSprites() {
        return llama_sprites;
    }
    
    public BufferedImage[] getMartilloSprites() {
        return martillo_sprites;
    }
    
    public BufferedImage[] getParaguasSprites() {
        return paraguas_sprites;
    }
    
    public BufferedImage[] getBolsoSprites() {
        return bolso_sprites;
    }
    
    public BufferedImage[] getSombreroSprites() {
        return sombrero_sprites;
    }
    
    // 🆕 NUEVOS GETTERS PARA VICTORIA
    public BufferedImage getCorazonSprite() {
        return spriteCorazon;
    }
    
    public BufferedImage getCorazonRotoSprite() {
        return spriteCorazonRoto;
    }
    
    public BufferedImage[] getDKAgarraSprites() {
        return spritesDKAgarra;
    }
    
    public int getNivelActual() {
        return nivelActual;
    }
}