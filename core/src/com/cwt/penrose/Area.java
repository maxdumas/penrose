package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by Max on 6/5/2014.
 */
class Area {
    private final List<Piece> pieces = new ArrayList<Piece>();
    private final HashMap<Piece, Piece[]> adj = new HashMap<Piece, Piece[]>();
    private final int ownerId;

    public Area(int ownerId) {
        this.ownerId = ownerId;
    }

    public void draw(SpriteBatch batch) {
        for (Piece piece : pieces)
            piece.draw(batch);
    }

    public Piece getPiece(int x, int y) {
        int r = MathUtils.round((Piece.SQRT_3 * x - y) / (3f * Piece.RADIUS));
        int g = MathUtils.round((-Piece.SQRT_3 * x - y) / (3f * Piece.RADIUS));
        int b = -(r + g);

        for(Piece p : pieces)
            if(p.r == r && p.g == g && p.b == b)
                return p;

        return null;
    }

    /**
     *
     * @param p the piece to add
     * @param n the neighbors of the piece to be added
     */
    private void addPiece(Piece p, Piece[] n) {
        pieces.add(p);
        for(int i = 0; i < 6; ++i)
            if(n[i] != null)
                // Get the adjacent piece, calculate the edge it must neighbor p on, then set that edge's neighbor to p.
                adj.get(n[i])[(i + 3) % 6] = p;
        adj.put(p, n);
    }

    public void removePiece(Piece p) {
        pieces.remove(p);
        Piece[] n = adj.get(p);
        if(n == null) return;
        for(int i = 0; i < 6; ++i)
            if(n[i] != null)
                // Get the adjacent piece, calculate the edge it must neighbor p on, then set that edge's neighbor to null.
                adj.get(n[i])[(i + 3) % 6] = null;
        adj.remove(p);
    }

    /**
     * Given a piece p, returns true if that piece is added to the area in a valid way,
     * returns false if that piece could not be added to the area.
     * @param p the piece to add
     * @return true if the piece is a valid configuration and was thus added, false otherwise
     */
    public boolean addPieceIfValid(Piece p, int playerId) {
        if (ownerId == playerId && pieces.isEmpty() && PieceType.isRoom(p)) {
            addPiece(p, new Piece[6]);
            return true;
        }

        Piece[] neighbors = new Piece[6];
        for (Piece q : pieces) {
            // We don't want to be able to place any piece that blocks a connector
            // So if there is any piece that is not mutually adjacent to our new piece , then
            // this move is not valid.
            if (q.adjacentEdge(p) >= 0) { // Check if the two pieces share an adjacent edge
                int pe = p.adjacentEdge(q);
                if (pe >= 0) { // Ensure any connection is mutual
                    neighbors[pe] = q;
                } else return false;
            } else if (p.adjacentEdge(q) >= 0) // Ensure any non-connection is mutual
                return false;
        }
        addPiece(p, neighbors); // Piece passed preliminary validation, now validate it in the context of the larger area

        if(!validate(playerId)) {
            removePiece(p);
            return false;
        } else return true;
    }

    /**
     * Validates the current state of the area and checks if a victory condition has been met.
     * @return true if area state is valid, false otherwise.
     */
    public boolean validate(int playerId) {
        // The number of connected rooms (we always start with 1 room because the first piece is always a room
        int nRooms = 1;
        // The type of the edge of the last room from which we projected our search
        EdgeType outgoing = null;
        // The set of pieces that have been visited as keys, with their parents in the DFS tree as the value
        HashMap<Piece, Piece> visited = new HashMap<Piece, Piece>(pieces.size());
        Stack<Piece> s = new Stack<Piece>();
        s.push(pieces.get(0));
        visited.put(pieces.get(0), null);

        while (!s.isEmpty()) {
            Piece v = s.pop();
            Piece[] n = adj.get(v);
            for (int i = 0; i < 6; ++i)
                if (n[i] != null) { // Ensure that this side actually has a neighbor
                    Piece w = n[i];
                    if (!visited.containsKey(w)) { // Presence in the map indicates that it was visited
                        if (PieceType.isRoom(v)) outgoing = v.edgeType(i);
                        if (PieceType.isRoom(w)) {
                            EdgeType incoming = w.edgeType((i + 3) % 6);
                            if (outgoing != EdgeType.ANY && outgoing == incoming)
                                return false; // Edges are not paired correctly, so we're done here
                            else ++nRooms; // Everything is good, so we've connected another room!
                        }
                        visited.put(w, v);
                        s.push(w);
                    }
                    // If v was already visited, we've found a cycle so we're only good if that cycle is connecting all 3 rooms
                    // unless w is the parent of v, in which case, false alarm!
                    else if (visited.get(v) != w) {
                        if (nRooms == 3) { // All 3 rooms are connected in a cycle! That means victory!
                            System.out.println("I WIN. YES!@!!!@#$@$ I, PLAYER " + ownerId + " WIN");
                            return true;
                        } else return false;
                    }
                }
        }
        return true;
    }

    public int getCenterX() {
        if(pieces.size() == 0) return 0;

        int sumX = 0;
        for(Piece p : pieces) {
            sumX += p.x;
        }
        return sumX / pieces.size();
    }

    public int getCenterY() {
        if(pieces.size() == 0) return 0;

        int sumY = 0;
        for(Piece p : pieces) {
            sumY += p.y;
        }
        return sumY / pieces.size();
    }
}
