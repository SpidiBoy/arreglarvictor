package Objetos.Utilidad;

import Objetos.GameObjetos;
import Objetos.Player;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handler mejorado con sistema de capas de renderizado
 * 
 * CAPAS DE RENDERIZADO (de atrás hacia adelante):
 * 1. FONDO      - TileVisual con esFondo=true, decoraciones lejanas
 * 2. BLOQUES    - Bloques sólidos, escaleras, plataformas
 * 3. ENTIDADES  - Jugador, enemigos, barriles, NPCs
 * 4. EFECTOS    - Explosiones, partículas, efectos visuales
 * 
 * @author LENOVO
 */
public class Handler {
    // Lista principal de objetos
    private List<GameObjetos> gameobjs;
    private Player player;
    private EstadoJuego estadoJuego;

    
    public Handler(){
        gameobjs = new LinkedList<GameObjetos>();
        this.estadoJuego = EstadoJuego.getInstance();
    }
    
    public void tick(){
        for(GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)){ 
            obj.tick();
        }
        if (estadoJuego != null) {
        estadoJuego.tick();
    }
    }
    
    /**
     * Renderizado por capas para control correcto del Z-order
     * Orden: Fondo → Bloques → Entidades → Efectos
     */
    public void render(Graphics g){
        // CAPA 1: FONDO (TileVisual marcados como fondo)
        renderCapa(g, ObjetosID.TileVisual, true);
        
        // CAPA 2: BLOQUES Y ESTRUCTURAS (sólidos y decorativos de primer plano)
        renderBloques(g);
        
        // CAPA 3: ENTIDADES (jugador, enemigos, NPCs)
        renderEntidades(g);
        
        // CAPA 4: EFECTOS (explosiones, partículas)
        renderEfectos(g);
        
        renderItems(g);
        // Renderizar HUD
        if (estadoJuego != null) {
        estadoJuego.renderHUD(g, 
            mariotest.Mariotest.getVentanaWidth(), 
            mariotest.Mariotest.getVentanaHeight()
        );
    }
    }
    
    private void renderItems(Graphics g) {
    for (GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)) {
        if (obj.getId() == ObjetosID.Item) {
            obj.render(g);
        }
    }
}
    
    /**
     * Renderiza una capa específica de TileVisual
     */
    private void renderCapa(Graphics g, ObjetosID id, boolean esFondo) {
        for (GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)) {
            if (obj.getId() == id) {
                // Para TileVisual, verificar si es fondo o no
                if (id == ObjetosID.TileVisual) {
                    try {
                        Objetos.TileVisual tile = (Objetos.TileVisual) obj;
                        if (tile.isEsFondo() == esFondo) {
                            obj.render(g);
                        }
                    } catch (ClassCastException e) {
                        // Si falla el cast, renderizar normalmente
                        obj.render(g);
                    }
                } else {
                    obj.render(g);
                }
                
            }
        }
    }
    
    /**
     * Renderiza bloques sólidos y escaleras (CAPA 2)
     */
    private void renderBloques(Graphics g) {
        for (GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)) {
            ObjetosID id = obj.getId();
            
            if (id == ObjetosID.Bloque || 
                id == ObjetosID.Pipe || 
                id == ObjetosID.Escalera || 
                id == ObjetosID.EscaleraRota) {
                obj.render(g);
            }
            
            // TileVisual de primer plano (NO fondo)
            if (id == ObjetosID.TileVisual) {
                try {
                    Objetos.TileVisual tile = (Objetos.TileVisual) obj;
                    if (!tile.isEsFondo()) {
                        obj.render(g);
                    }
                } catch (ClassCastException e) {
                    // Ignorar si falla
                }
            }
        }
    }
    
    /**
     * Renderiza entidades (jugador, enemigos, NPCs) - CAPA 3
     * Esta capa está ENCIMA de bloques y tiles visuales
     */
    private void renderEntidades(Graphics g) {
        for (GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)) {
            ObjetosID id = obj.getId();
            
            if (id == ObjetosID.Jugador || 
                id == ObjetosID.DiegoKong || 
                id == ObjetosID.Barril || 
                id == ObjetosID.Princesa || 
                id == ObjetosID.Fuego) {
                obj.render(g);
            }
        }
    }
    
    /**
     * Renderiza efectos visuales (CAPA 4)
     * Siempre en primer plano
     */
    private void renderEfectos(Graphics g) {
        for (GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)) {
            ObjetosID id = obj.getId();
            
            if (id == ObjetosID.Explosion || 
                id == ObjetosID.Particula || 
                id == ObjetosID.Puntos) {
                obj.render(g);
            }
        }
    }
    
    /**
     * Método alternativo: Renderizado simple por orden de adición
     * (Mantener como fallback si el sistema de capas falla)
     */
    public void renderSimple(Graphics g) {
        for(GameObjetos obj : new LinkedList<GameObjetos>(gameobjs)){
            obj.render(g);
        }
    }
    
    // ==================== MÉTODOS ORIGINALES ====================
    
    public void addObj(GameObjetos obj){
        gameobjs.add(obj);
    }
    
    public void removeObj(GameObjetos obj){
        gameobjs.remove(obj);
    }
    
    public List<GameObjetos> getGameObjs(){
        return gameobjs;
    } 
    
    public int setPlayer(Player player){
        if(this.player != null){
            return -1;
        }
      
        addObj(player);
        this.player = player;
        return 0;
    }
    
    public int removePlayer(){
        if(player == null){
            return -1;
        }
        
        removeObj(player);
        this.player = null;
        return 0;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    // ==================== MÉTODOS ÚTILES ====================
    
    /**
     * Obtiene todos los objetos de un tipo específico
     */
    public List<GameObjetos> getObjetosPorTipo(ObjetosID tipo) {
        List<GameObjetos> resultado = new ArrayList<>();
        for (GameObjetos obj : gameobjs) {
            if (obj.getId() == tipo) {
                resultado.add(obj);
            }
        }
        return resultado;
    }
    
    /**
     * Cuenta cuántos objetos hay de un tipo
     */
    public int contarObjetosPorTipo(ObjetosID tipo) {
        int count = 0;
        for (GameObjetos obj : gameobjs) {
            if (obj.getId() == tipo) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Elimina todos los objetos de un tipo específico
     */
    public void eliminarObjetosPorTipo(ObjetosID tipo) {
        gameobjs.removeIf(obj -> obj.getId() == tipo);
    }
    
    /**
     * Información de debug del handler
     */
    public String getInfoDebug() {
        return String.format(
            "Handler [Total: %d | Jugador: %s | Bloques: %d | Enemigos: %d]",
            gameobjs.size(),
            player != null ? "✓" : "✗",
            contarObjetosPorTipo(ObjetosID.Bloque),
            contarObjetosPorTipo(ObjetosID.Barril)
        );
    }
    
        public EstadoJuego getEstadoJuego() {
        return estadoJuego;
    }
}