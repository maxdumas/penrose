package com.cwt.penrose;

/**
 * Created by Max on 6/29/2014.
 */
public interface State<T> {
    public Command handleInput(PenroseGame game, T entity);
}
