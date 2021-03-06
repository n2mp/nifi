/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.service;

import java.util.Collection;
import java.util.Set;

import org.apache.nifi.annotation.lifecycle.OnAdded;
import org.apache.nifi.controller.ControllerServiceLookup;

/**
 *
 */
public interface ControllerServiceProvider extends ControllerServiceLookup {

    /**
     * Creates a new Controller Service of the specified type and assigns it the
     * given id. If <code>firstTimeadded</code> is true, calls any methods that
     * are annotated with {@link OnAdded}
     *
     * @param type of service
     * @param id of service
     * @param firstTimeAdded for service
     * @return the service node
     */
    ControllerServiceNode createControllerService(String type, String id, boolean firstTimeAdded);

    /**
     * @param id of the service
     * @return the controller service node for the specified identifier. Returns
     * <code>null</code> if the identifier does not match a known service
     */
    ControllerServiceNode getControllerServiceNode(String id);

    /**
     * Removes the given Controller Service from the flow. This will call all
     * appropriate methods that have the @OnRemoved annotation.
     *
     * @param serviceNode the controller service to remove
     *
     * @throws IllegalStateException if the controller service is not disabled
     * or is not a part of this flow
     */
    void removeControllerService(ControllerServiceNode serviceNode);

    /**
     * Enables the given controller service that it can be used by other
     * components
     *
     * @param serviceNode the service node
     */
    void enableControllerService(ControllerServiceNode serviceNode);

    /**
     * Enables the collection of services. If a service in this collection
     * depends on another service, the service being depended on must either
     * already be enabled or must be in the collection as well.
     *
     * @param serviceNodes the nodes
     */
    void enableControllerServices(Collection<ControllerServiceNode> serviceNodes);

    /**
     * Disables the given controller service so that it cannot be used by other
     * components. This allows configuration to be updated or allows service to
     * be removed.
     *
     * @param serviceNode the node
     */
    void disableControllerService(ControllerServiceNode serviceNode);

    /**
     * @return a Set of all Controller Services that exist for this service
     * provider
     */
    Set<ControllerServiceNode> getAllControllerServices();

    /**
     * Verifies that all running Processors and Reporting Tasks referencing the
     * Controller Service (or a service that depends on the provided service)
     * can be stopped.
     *
     * @param serviceNode the node
     *
     * @throws IllegalStateException if any referencing component cannot be
     * stopped
     */
    void verifyCanStopReferencingComponents(ControllerServiceNode serviceNode);

    /**
     * Recursively unschedules all schedulable components (Processors and
     * Reporting Tasks) that reference the given Controller Service. For any
     * Controller services that reference this one, its schedulable referencing
     * components will also be unscheduled.
     *
     * @param serviceNode the node
     */
    void unscheduleReferencingComponents(ControllerServiceNode serviceNode);

    /**
     * Verifies that all Controller Services referencing the provided Controller
     * Service can be disabled.
     *
     * @param serviceNode the node
     *
     * @throws IllegalStateException if any referencing service cannot be
     * disabled
     */
    void verifyCanDisableReferencingServices(ControllerServiceNode serviceNode);

    /**
     * Disables any Controller Service that references the provided Controller
     * Service. This action is performed recursively so that if service A
     * references B and B references C, disabling references for C will first
     * disable A, then B.
     *
     * @param serviceNode the node
     */
    void disableReferencingServices(ControllerServiceNode serviceNode);

    /**
     * Verifies that all Controller Services referencing the provided
     * ControllerService can be enabled.
     *
     * @param serviceNode the node
     *
     * @throws IllegalStateException if any referencing component cannot be
     * enabled
     */
    void verifyCanEnableReferencingServices(ControllerServiceNode serviceNode);

    /**
     * Enables all Controller Services that are referencing the given service.
     * If Service A references Service B and Service B references serviceNode,
     * Service A and B will both be enabled.
     *
     * @param serviceNode the node
     */
    void enableReferencingServices(ControllerServiceNode serviceNode);

    /**
     * Verifies that all enabled Processors referencing the ControllerService
     * (or a service that depends on the provided service) can be scheduled to
     * run.
     *
     * @param serviceNode the node
     *
     * @throws IllegalStateException if any referencing component cannot be
     * scheduled
     */
    void verifyCanScheduleReferencingComponents(ControllerServiceNode serviceNode);

    /**
     * Schedules any schedulable component (Processor, ReportingTask) that is
     * referencing the given Controller Service to run. This is performed
     * recursively, so if a Processor is referencing Service A, which is
     * referencing serviceNode, then the Processor will also be started.
     *
     * @param serviceNode the node
     */
    void scheduleReferencingComponents(ControllerServiceNode serviceNode);
}
