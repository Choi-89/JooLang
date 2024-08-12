//src\customAxios.js
import axios from 'axios';
import { My } from './configuration/web/WebConfig.ts';

let customAxios;
export default customAxios = function customAxios(url: string, callback: (data: any) => void) {
    const my = new My();
    axios({
        url: '/test' + url,
        method: 'post',
        baseURL: `http://${my.ipAddress}:${my.backEndPort}`,
        withCredentials: true,
    }).then(function (response) {
        callback(response.data);
    });
}