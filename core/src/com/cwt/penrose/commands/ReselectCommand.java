package com.cwt.penrose.commands;

import com.cwt.penrose.PenroseGame;
import com.cwt.penrose.PlayerManager;
import com.cwt.penrose.PlayerState;

public class ReselectCommand implements Command {
    private final PenroseGame game;
    private final PlayerManager cpm;

    public ReselectCommand(PenroseGame game, PlayerManager cpm) {
        this.game = game;
        this.cpm = cpm;
    }

    @Override
    public boolean execute() {
        game.ghostVisible = true;
        game.ghostInvalid = false;

        cpm.setState(PlayerState.POSITIONING);

        return true;
    }

    @Override
    public boolean undo() {
        // Nothing to undo here?
        // Determine if things like ghost flags should be undone or not.
        return true;
    }

    @Override
    public int getAPCost() {
        return 0;
    }
}