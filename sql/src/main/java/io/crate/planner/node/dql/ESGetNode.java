/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.planner.node.dql;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import io.crate.analyze.relations.PlannedAnalyzedRelation;
import io.crate.analyze.relations.RelationVisitor;
import io.crate.exceptions.ColumnUnknownException;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.ReferenceInfo;
import io.crate.planner.node.PlanVisitor;
import io.crate.planner.symbol.Field;
import io.crate.planner.symbol.Symbol;
import io.crate.types.DataType;
import org.elasticsearch.common.Nullable;

import java.util.List;


public class ESGetNode extends ESDQLPlanNode implements DQLPlanNode, PlannedAnalyzedRelation {

    private final String index;
    private final List<String> ids;
    private final List<String> routingValues;
    private final List<Symbol> sortSymbols;
    private final boolean[] reverseFlags;
    private final Boolean[] nullsFirst;
    private final Integer limit;
    private final int offset;
    private final List<ReferenceInfo> partitionBy;

    private final static boolean[] EMPTY_REVERSE_FLAGS = new boolean[0];
    private final static Boolean[] EMPTY_NULLS_FIRST = new Boolean[0];

    public ESGetNode(String index,
                     List<Symbol> outputs,
                     List<DataType> outputTypes,
                     List<String> ids,
                     List<String> routingValues,
                     @Nullable List<Symbol> sortSymbols,
                     @Nullable boolean[] reverseFlags,
                     @Nullable Boolean[] nullsFirst,
                     @Nullable Integer limit,
                     int offset,
                     @Nullable List<ReferenceInfo> partitionBy) {
        this.index = index;
        this.outputs = outputs;
        outputTypes(outputTypes);
        this.ids = ids;
        this.routingValues = routingValues;
        this.sortSymbols = Objects.firstNonNull(sortSymbols, ImmutableList.<Symbol>of());
        this.reverseFlags = Objects.firstNonNull(reverseFlags, EMPTY_REVERSE_FLAGS);
        this.nullsFirst = Objects.firstNonNull(nullsFirst, EMPTY_NULLS_FIRST);
        this.limit = limit;
        this.offset = offset;
        this.partitionBy = Objects.firstNonNull(partitionBy, ImmutableList.<ReferenceInfo>of());
    }

    public String index() {
        return index;
    }

    @Override
    public <C, R> R accept(PlanVisitor<C, R> visitor, C context) {
        return visitor.visitESGetNode(this, context);
    }

    public List<String> ids() {
        return ids;
    }

    public List<String> routingValues() {
        return routingValues;
    }

    @Nullable
    public Integer limit() {
        return limit;
    }

    public int offset() {
        return offset;
    }

    public List<Symbol> sortSymbols() {
        return sortSymbols;
    }

    public boolean[] reverseFlags() {
        return reverseFlags;
    }

    public Boolean[] nullsFirst() {
        return nullsFirst;
    }


    public List<ReferenceInfo> partitionBy() {
        return partitionBy;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("index", index)
                .add("ids", ids)
                .add("outputs", outputs)
                .add("partitionBy", partitionBy)
                .toString();
    }

    @Override
    public <C, R> R accept(RelationVisitor<C, R> visitor, C context) {
        return visitor.visitPlanedAnalyzedRelation(this, context);
    }

    @javax.annotation.Nullable
    @Override
    public Field getField(ColumnIdent path) {
        throw new UnsupportedOperationException("getField is not supported on ESGetNode");
    }

    @Override
    public Field getWritableField(ColumnIdent path) throws UnsupportedOperationException, ColumnUnknownException {
        throw new UnsupportedOperationException("getWritableField is not supported on ESGetNode");
    }

    @Override
    public List<Field> fields() {
        throw new UnsupportedOperationException("fields is not supported on ESGetNode");
    }
}
