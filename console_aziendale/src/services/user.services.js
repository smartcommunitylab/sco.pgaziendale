import axios from "axios";

export const userService = {
    login,
    loginOAuth,
    logout,
    getAccount,
    update,
    getCompanies,
    changePassword,
    resetPasswordInit,
    resetPasswordFinish
};

function login(username, password) {
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
function loginOAuth(access_token) {
    return axios.get(process.env.VUE_APP_BASE_URL + 'authenticate/extjwt', {
        headers: {
            Authorization: access_token
        }
    }).then(
        res => {
            if (res && res.data && res.data.id_token) {
                localStorage.setItem('token', res.data.id_token);
                localStorage.setItem('oauth_login', true);
                return Promise.resolve(res.data.id_token);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

function getAccount() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API).then(
        user => {
            if (user && user.data) {
                localStorage.setItem('user', JSON.stringify(user.data));
                return Promise.resolve(user.data);
            }
            return Promise.reject();
        }, () => {
            return Promise.reject();
        }

    )
}
function logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.removeItem('oauth_login');
    sessionStorage.clear();
    localStorage.clear();
}
function update(user) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API, user).then(
        () => {
            Promise.resolve();
        }, () => {
            Promise.reject();
        }
    )
}

function getCompanies(user) {
    var companies = [];
    for (var i = 0; i < user.roles.length; i++) {
        if (user.roles[i].companyId) {
            companies.push(user.roles[i].companyId);
        }
    }
    return companies;
}

function changePassword(oldPassword, newPassword) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_CHANGE_PASSWORD_API, 
    { 
    "currentPassword": oldPassword, 
    "newPassword": newPassword
 }).then(
        res => {
            if (res && res.data ) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
function resetPasswordInit(username) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_INIT_RESET_PASSWORD_API,
        '"'+username+'"',{
        headers: {
            'Content-Type': 'application/json',
            'Accept':'application/json',
        }}
    ).then(
        res => {
                return Promise.resolve(res.data);

        }, err => {
            return Promise.reject(err);
        }

    )
}

function resetPasswordFinish(key, newPassword) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_GET_ACCOUNT_API + '/' + process.env.VUE_APP_FINISH_RESET_PASSWORD_API, {
        "key": key,
        "newPassword": newPassword
      }).then(
        res => {
               return Promise.resolve(res.data);

        }, err => {
            return Promise.reject(err);
        }

    )
}



