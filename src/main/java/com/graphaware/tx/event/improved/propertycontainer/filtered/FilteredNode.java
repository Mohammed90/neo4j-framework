/*
 * Copyright (c) 2013 GraphAware
 *
 * This file is part of GraphAware.
 *
 * GraphAware is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.graphaware.tx.event.improved.propertycontainer.filtered;

import com.graphaware.propertycontainer.wrapper.NodeWrapper;
import com.graphaware.tx.event.improved.strategy.InclusionStrategies;
import com.graphaware.tx.event.improved.strategy.PropertyInclusionStrategy;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 * {@link FilteredPropertyContainer} which is a {@link org.neo4j.graphdb.Node}.
 */
public class FilteredNode extends FilteredPropertyContainer<Node> implements Node, NodeWrapper {

    /**
     * Create a new filtering node decorator.
     *
     * @param wrapped    decorated node.
     * @param strategies for filtering.
     */
    public FilteredNode(Node wrapped, InclusionStrategies strategies) {
        super(wrapped, strategies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PropertyInclusionStrategy<Node> getPropertyInclusionStrategy() {
        return strategies.getNodePropertyInclusionStrategy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Relationship> getRelationships() {
        return filtered(super.getRelationships());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Relationship> getRelationships(RelationshipType... types) {
        return filtered(super.getRelationships(types));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Relationship> getRelationships(Direction direction, RelationshipType... types) {
        return filtered(super.getRelationships(direction, types));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Relationship> getRelationships(Direction dir) {
        return filtered(super.getRelationships(dir));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Relationship> getRelationships(RelationshipType type, Direction dir) {
        return filtered(super.getRelationships(type, dir));
    }

    /**
     * Create a filtering iterable for relationships.
     *
     * @param original iterable.
     * @return filtering iterable.
     */
    private Iterable<Relationship> filtered(Iterable<Relationship> original) {
        return new FilteredRelationshipIterator(original, strategies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Relationship createRelationshipTo(Node otherNode, RelationshipType type) {
        return new FilteredRelationship(super.createRelationshipTo(otherNode, type), strategies);
    }
}