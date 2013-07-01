package com.github.timmystorms.collaborations.entity.json;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.github.timmystorms.collaborations.entity.Collaboration;

public class SimpleCollaboration {

    private Long id;

    private String name;

    private Collaboration.Visibility visibility;

    public SimpleCollaboration() {
    }

    public SimpleCollaboration(final Long id, final String name, final Collaboration.Visibility visibility) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Collaboration.Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(final Collaboration.Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1, 3).append(id).append(name).append(visibility).toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SimpleCollaboration)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        final SimpleCollaboration entity = (SimpleCollaboration) obj;
        return new EqualsBuilder().append(id, entity.id).append(name, entity.name)
                .append(visibility, entity.visibility).isEquals();
    }

}
