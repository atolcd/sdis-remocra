import axios from "axios";

export async function getSrid() {
    let srid;
    await axios.get('/remocra/parametre/srid').then(response => srid = response.data );
    let res = parseInt(srid);
    return !isNaN(res) ? res : null;
}
