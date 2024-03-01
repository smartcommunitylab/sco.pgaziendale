import axios from "axios";

export const companyService = {
    getAllCompanies,
    getCompanyById,
    addCompany,
    updateCompany,
    updateCompanyState,
    deleteCompany,
    getUsers,
    addUser,
    updateUser,
    deleteUser
};
//get all the companies
function getAllCompanies() {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API,{
        params: {
          size: 1000
        }
      }).then(
        res => {
            if (res && res.data && res.data.content) {
                return Promise.resolve(res.data.content);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}

//get company byid
function getCompanyById(companyId) {
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+companyId).then(
        res => {
            if (res && res.data && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
//create a new company
function addCompany(company){
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API,company).then(
        res => {
            if (res && res.data && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function updateCompany(company){
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id,company).then(
        res => {
            if (res && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company state
function updateCompanyState(company){
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id +'/'+company.state).then(
        res => {
            if (res && res.data) {
                return Promise.resolve(res.data);
            }
            else return Promise.reject(null);
        }, err => {
            return Promise.reject(err);
        }

    )
}
// delete an old company
function deleteCompany(company){
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id).then(
        res => {
            if (res) {
                return Promise.resolve(company);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}
function getRole(role){
    switch (role) {
        case 'ROLE_CAMPAIGN_MANAGER':
            return 'Amministratore delle campagne';
        case 'ROLE_TERRITORY_ADMIN':
            return 'Amministratore del territorio';
        case 'ROLE_MOBILITY_MANAGER':
            return 'Mobility Manager';
        default:
            break;
    }
}
//get administrators
function translateRole(roles) {
    let returnString = "";
    roles.forEach((ele) => {
      returnString += getRole(ele.role) + " ";
    });
    return returnString;
  }
  function computedNewRoles(items) {
    if (items) {
        if(Array.isArray(items)){
            items.forEach((element) => {
                element.rolesComputed = translateRole(element.roles);
            });
        }else{
            items.rolesComputed = translateRole(items.roles);
        }
    }
    return items;
  }
function getUsers(company){
    return axios.get(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API+'/'+company.id+'/'+process.env.VUE_APP_USERS_API).then(
        res => {
            if (res) {
                if (res && res.data ) {
                let retData = computedNewRoles(res.data)
                return Promise.resolve(retData);
                }
                 else return Promise.reject(null);

            }
        }, err => {
            return Promise.reject(err);
        }

    )
}

function addUser(companyId, user) {
    return axios.post(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API, user).then(
        res => {
            if (res && res.data ) {
                let retData = computedNewRoles(res.data)
                return Promise.resolve(retData);
            }
            else return Promise.reject(null);

        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function updateUser(companyId, user) {
    return axios.put(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API , user).then(
        res => {
            if (res && res.data ) {
                let retData = computedNewRoles(res.data)
                return Promise.resolve(retData);
            }
            else return Promise.reject(null);

        }, err => {
            return Promise.reject(err);
        }

    )
}
// update an old company
function deleteUser(companyId, user) {
    return axios.delete(process.env.VUE_APP_BASE_URL + process.env.VUE_APP_COMPANIES_API + '/' + companyId + '/' + process.env.VUE_APP_USERS_API + '/' + user.username).then(
        res => {
            if (res) {
                return Promise.resolve(user.id);
            }
        }, err => {
            return Promise.reject(err);
        }

    )
}






