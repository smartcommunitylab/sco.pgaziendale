import axios from "axios";
import { BASE_URL, USER_API } from '../variables.js'
export default   {

    getUser() {
        return  axios.get(BASE_URL+USER_API)
    }
  
  }