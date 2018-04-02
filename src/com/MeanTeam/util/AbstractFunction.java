package com.MeanTeam.util;

public interface AbstractFunction
{
    void execute();

    AbstractFunction NULL = () -> {};
}
