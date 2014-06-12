package com.cwt.penrose;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

/**
 * Created by Max on 6/5/2014.
 */
public class HexBoard {
    final List<Piece> pieces = new ArrayList<Piece>();
    final HashMap<Piece, Piece[]> adj = new HashMap<Piece, Piece[]>();

    public void draw(SpriteBatch batch) {
        for (Piece piece : pieces)
            piece.draw(batch);
    }

    // returns false if placement failed
    public boolean placePiece(Piece newPiece) {
        Piece p = new Piece(newPiece);
        if (pieces.isEmpty()) {
            addPiece(p, new Piece[6]);
            return true;
        }
        boolean connectionMade = false;
        Piece[] n = new Piece[6];
        for (Piece q : pieces) {
            // We don't want to be able to place any piece that blocks a connector
            // So if there is any piece that is not mutually adjacent to our new piece , then
            // this move is not valid.
            if (q.adjacentEdge(p) >= 0) {
                int pe = p.adjacentEdge(q);
                if (pe >= 0) {
                    connectionMade = true;
                    n[pe] = q;
                } else return false;
            } else if (p.adjacentEdge(q) >= 0) return false;
        }
        if (connectionMade) {
            addPiece(p, n);
            if (!validate()) {
                removePiece(p);
                return false;
            }
            return true;
        }
        return false;
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

    private void removePiece(Piece p) {
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
     * This method validates the current state of the board and checks if a victory condition has been met.
     * @return
     */
    private boolean validate() {
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
                if (n[i] != null) {
                    Piece w = n[i];
                    if (!visited.containsKey(w)) {
                        if (PieceArchetype.isNode(v)) outgoing = v.edgeType(i);

                        if (PieceArchetype.isNode(w)) {
                            EdgeType incoming = w.edgeType((i + 3) % 6);
                            if (outgoing != EdgeType.ANY && outgoing == incoming)
                                return false; // Edges are not pair correctly, so we're done here
                            else ++nRooms; // Everything is good, so we've connected another room!
                        }
                        visited.put(w, v);
                        s.push(w);
                    }
                    // If v was already visited, we've found a cycle so we're only good if that cycle is connecting all 3 rooms
                    // unless w is the parent of v, in which case, false alarm!
                    else if (visited.get(v) != w) {
                        if (nRooms == 3) { // All 3 rooms are connected in a cycle! That means victory!
                            System.out.println("I WIN. YES!@!!!@#$@$");
                            return true;
                        } else return false;
                    }
                }
        }
        return true;
    }
}
