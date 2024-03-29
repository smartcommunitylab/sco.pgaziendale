/*******************************************************************************
 * Copyright 2015 Fondazione Bruno Kessler
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/

package it.smartcommunitylab.pgazienda.domain;

import it.smartcommunitylab.pgazienda.util.TrackUtils;

/**
 * @author raman
 *
 */
public class Circle extends Shape {

	private double[] center;
	private double radius;

	public Circle() {
	}
	public Circle(double[] center, double radius) {
		super();
		this.center = center;
		this.radius = radius;
	}


	public double[] getCenter() {
		return center;
	}

	public void setCenter(double[] center) {
		this.center = center;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public boolean inside(double lat, double lon) {
		// consider the radius in meters!
		return TrackUtils.harvesineDistance(center[0], center[1], lat, lon) <= radius / 1000;
	}

}