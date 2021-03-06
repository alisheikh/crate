/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package io.crate.operation.scalar.cast;

import io.crate.metadata.FunctionIdent;
import io.crate.metadata.FunctionImplementation;
import io.crate.metadata.Functions;
import io.crate.operation.scalar.ScalarFunctionModule;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ToTimestampFunctionTest {

    private Functions functions;

    private String functionName = ToTimestampFunction.NAME;

    @Before
    public void setUp() {
        functions = new ModulesBuilder()
                .add(new ScalarFunctionModule()).createInjector().getInstance(Functions.class);
    }

    @Test
    public void testResolveFunction() throws Throwable {
        List<DataType> supportedTypes = Arrays.<DataType>asList(DataTypes.TIMESTAMP,
                DataTypes.SHORT,
                DataTypes.INTEGER,
                DataTypes.LONG,
                DataTypes.FLOAT,
                DataTypes.DOUBLE,
                DataTypes.STRING
                );
        for (DataType dataType : supportedTypes) {
            FunctionIdent ident = new FunctionIdent(functionName, Arrays.asList(dataType));
            FunctionImplementation implementation = functions.get(ident);
            assertThat(implementation, instanceOf(ToTimestampFunction.class));
            assertEquals(((ToTimestampFunction) implementation).returnType, DataTypes.TIMESTAMP);
        }
    }

    @Test
    public void testResolveFunctionUnsupportedTypes() throws Throwable {
        List<DataType> unsupportedTypes = Arrays.<DataType>asList(
                DataTypes.GEO_POINT,
                DataTypes.GEO_SHAPE,
                DataTypes.OBJECT,
                DataTypes.BOOLEAN
        );
        for (DataType dataType : unsupportedTypes) {
            try {
                FunctionIdent ident = new FunctionIdent(functionName, Arrays.asList(dataType));
                functions.get(ident);
            } catch (Exception e) {
                assertEquals(e.getMessage(), String.format("type '%s' not supported for conversion", dataType.getName()));
            }
        }
    }

}
