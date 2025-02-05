/* (C)2025 */
package io.github.roiocam.jsm.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public abstract class Parent<T, R> extends GrandParent<BigDecimal> {
    private List<T> friends;

    private Set<R> permissions;

    public List<T> getFriends() {
        return friends;
    }

    public void setFriends(List<T> friends) {
        this.friends = friends;
    }

    public Set<R> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<R> permissions) {
        this.permissions = permissions;
    }
}
