import axios from "axios";

const BASE_URL = process.env.VUE_APP_BASE_URL;

export const h3Service = {
  getDurationMap,
  getUserDepartureMap,
  getTripsMap
};

function buildParams(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
  return { territory_id, campaign_id, target_resolution: resolution, min_tracks, h3_cell, is_departure };
}

function getDurationMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
  return axios.get(`${BASE_URL}${process.env.VUE_APP_H3_AVG_DURATION_API}`, {
    params: buildParams(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure)
  }).then(res => res.data);
}

function getUserDepartureMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
  return axios.get(`${BASE_URL}${process.env.VUE_APP_H3_USER_DEPARTURE_API}`, {
    params: buildParams(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure)
  }).then(res => res.data);
}

function getTripsMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
  return axios.get(`${BASE_URL}${process.env.VUE_APP_H3_TRIPS_API}`, {
    params: buildParams(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure)
  }).then(res => res.data);
}