package com.grepp.nbe1_3_team04.stadium.util

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point

object PositionUtil {

    private val geometryFactory = GeometryFactory()

    fun createPoint(latitude: Double, longitude: Double): Point {
        return geometryFactory.createPoint(Coordinate(longitude, latitude))
    }
}