import axios from "axios";

export const h3Service = {
  getDurationMap,
  getUserDepartureMap,
  getTripsMap
};

function getDurationMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
  return axios.get(`${process.env.VUE_APP_BASE_URL}geo/duck/avg-duration`, {
    params: {
      territory_id,
      campaign_id,
      target_resolution: resolution,
      min_tracks,
      h3_cell,
      is_departure
    }
  }).then(res => res.data);
}
  function getUserDepartureMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
    return axios.get(`${process.env.VUE_APP_BASE_URL}geo/duck/avg-duration`, {
      params: {
        territory_id,
        campaign_id,
        target_resolution: resolution,
        min_tracks,
        h3_cell,
        is_departure
      }
    }).then(res => res.data);
  }
    function getTripsMap(territory_id, campaign_id, resolution, min_tracks, h3_cell, is_departure) {
        return axios.get(`${process.env.VUE_APP_BASE_URL}geo/duck/avg-duration`, {
          params: {
            territory_id,
            campaign_id,
            target_resolution: resolution,
            min_tracks,
            h3_cell,
            is_departure
          }
        }).then(res => res.data);
}
  