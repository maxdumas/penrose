package com.cwt.penrose;

import com.cwt.penrose.commands.Command;

/**
 * Created by Max on 6/29/2014.
 */
public interface State<T> {
    public Command handleInput(final PenroseGame game, final T entity);
}
