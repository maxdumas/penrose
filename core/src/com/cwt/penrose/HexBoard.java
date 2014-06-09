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
        // We want to ensure that there is no path from an edge of a room of one type to a same-type edge in another room
        // We know that the first piece will always be a room, so we want to start searching from there.
        // We will use a depth-first search for this purpose
        // We also want to check if all three rooms have been connected.

        int nRooms = 1;
        HashSet<Piece> visited = new HashSet<Piece>(pieces.size()); //Presence indicates that the piece has been visited
        Stack<Piece> s = new Stack<Piece>();
        s.push(pieces.get(0));
        visited.add(pieces.get(0));
        EdgeType outEdge = null, inEdge = null;

        while(!s.isEmpty()) {
            Piece p = s.peek();
            Piece child = null;
            Piece[] n = adj.get(p);
            for(int i = 0; i < 6; ++i)
                if(!visited.contains(n[i])) {
                    child = n[i];
                    if(child != null) {
                        if (PieceArchetype.NODES.contains(p.type))
                            outEdge = p.edgeState(i);
                        inEdge = child.edgeState((i + 3) % 6);
                        break;
                    }
                }

            if(child != null) {
                visited.add(child);
                // If this occurs then we have a node mismatch and shit is borked
                if(PieceArchetype.NODES.contains(child.type)) {
                    if(outEdge == EdgeType.IN && inEdge == EdgeType.IN || outEdge == EdgeType.OUT && inEdge == EdgeType.OUT)
                        return false;
                    else ++nRooms;
                }
                s.push(child);
            } else s.pop();
        }
        if(nRooms == 3) System.out.println("VICTORY!!!!!!!!!!!!!!!!!!!!!");
        return true;
    }
}
