package com.fabiano.bloomify;

import jdk.jshell.spi.ExecutionControl;

import java.util.List;

public class Bloomify {
    public Bloomify(int expectedInsertions, double falsePositiveRate) {

    }

    public boolean contains(String value) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not implemented");
    }

    public void add(String element) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not implemented");
    }

    public void addAll(List<String> elements) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("not implemented");
    }
}
