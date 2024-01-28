package org.bvkatwijk.lens.ast;

public record Type(String pack, String name) {
    public String qualified() {
        return pack + "." + name;
    }
}
