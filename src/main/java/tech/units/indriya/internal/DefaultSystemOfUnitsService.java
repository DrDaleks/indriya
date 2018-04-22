/*
 * Units of Measurement Reference Implementation
 * Copyright (c) 2005-2018, Jean-Marie Dautelle, Werner Keil, Otavio Santana.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of JSR-385, Indriya nor the names of their contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package tech.units.indriya.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.BinaryPrefix;
import javax.measure.MetricPrefix;
import javax.measure.Prefix;
import javax.measure.spi.SystemOfUnits;
import tech.units.indriya.spi.AbstractSystemOfUnitsService;
import tech.units.indriya.unit.Units;

/**
 * @author <a href="mailto:units@catmedia.us">Werner Keil</a>
 * @author Martin Desruisseaux
 * @version 0.8, April 13, 2018
 * @since 1.0
 */
public class DefaultSystemOfUnitsService extends AbstractSystemOfUnitsService {
	
	public DefaultSystemOfUnitsService() {
		souMap.put(Units.class.getSimpleName(), Units.getInstance());
	}

	@Override
	public SystemOfUnits getSystemOfUnits() {
		return getSystemOfUnits(Units.class.getSimpleName());
	}

	@Override @Deprecated //TODO [ahuber] currently subject of discussion whether to remove entirely 
	public Set<Prefix> getPrefixes(Class<? extends Prefix> prefixType) {
		if(!Prefix.class.isAssignableFrom(prefixType)) {
			throw new ClassCastException(String.format("Not a prefix type: %s", prefixType));
		}
		if(MetricPrefix.class.equals(prefixType)) {
			return Stream.of(MetricPrefix.values()).collect(Collectors.toSet()); 
		}
		if(BinaryPrefix.class.equals(prefixType)) {
			return Stream.of(BinaryPrefix.values()).collect(Collectors.toSet()); 
		}
		if(Prefix.class.equals(prefixType)) {
			Stream.concat( Stream.of(MetricPrefix.values()), Stream.of(BinaryPrefix.values()))
			.collect(Collectors.toSet()); 
		}
		return Collections.emptySet();
	}
}
