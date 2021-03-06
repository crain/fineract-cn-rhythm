/*
 * Copyright 2017 Kuelap, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.rhythm.service.rest;

import io.mifos.anubis.annotation.AcceptedTokenType;
import io.mifos.anubis.annotation.Permittable;
import io.mifos.core.command.gateway.CommandGateway;
import io.mifos.rhythm.api.v1.domain.ClockOffset;
import io.mifos.rhythm.service.internal.command.ChangeClockOffsetCommand;
import io.mifos.rhythm.service.internal.service.ClockOffsetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.mifos.core.lang.config.TenantHeaderFilter.TENANT_HEADER;

/**
 * @author Myrle Krantz
 */
@RestController
@RequestMapping("/clockoffset")
public class ClockOffsetRestController {
  private final CommandGateway commandGateway;
  private final ClockOffsetService clockOffsetService;

  @Autowired
  public ClockOffsetRestController(
      final CommandGateway commandGateway,
      final ClockOffsetService clockOffsetService) {
    super();
    this.commandGateway = commandGateway;
    this.clockOffsetService = clockOffsetService;
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<ClockOffset> getClockOffset(@RequestHeader(TENANT_HEADER) final String tenantIdentifier) {
    return ResponseEntity.ok(this.clockOffsetService.findByTenantIdentifier(tenantIdentifier));
  }

  @Permittable(value = AcceptedTokenType.SYSTEM)
  @RequestMapping(
      method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> setClockOffset(
      @RequestHeader(TENANT_HEADER) final String tenantIdentifier,
      @RequestBody @Valid final ClockOffset instance) throws InterruptedException {
    this.commandGateway.process(new ChangeClockOffsetCommand(tenantIdentifier, instance));
    return ResponseEntity.accepted().build();
  }
}
