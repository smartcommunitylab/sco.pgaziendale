import { UserManager } from 'oidc-client-ts';

const loc = `${location.protocol}//${location.hostname}${location.port?':'+location.port:''}`;
const settings = {
  authority: process.env.VUE_APP_OAUTH_AUTHORITY,
  client_id: process.env.VUE_APP_OAUTH_CLIENT_ID,
  redirect_uri: `${loc}/Callback`,
  post_logout_redirect_uri: `${loc}`,
  response_type: 'code',
  scope: 'email openid profile'
};
const userManager = new UserManager(settings);

export const oauthService = {
    signin() {
        return userManager.signinRedirect();
    },
    callback() {
        return userManager.signinCallback();
    },
    signout() {
        return userManager.signoutRedirect();
    }
};