import axios from "axios";

export const statService = {
    getStat

};

function getStat(username, password) {
    console.log(process.env.VUE_APP_BASE_URL);
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_LOGIN_API, {
        "password": password,
        "rememberMe": true,
        "username": username
    }).then(
        res => {
            if (res && res.data && res.data.id_token) {
                localStorage.setItem('token', res.data.id_token);
                return Promise.resolve(res.data.id_token);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}



