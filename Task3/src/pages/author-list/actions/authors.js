import axios from '../../../misc/requests';
import config from '../../../config';
import {
    FETCH_AUTHORS_PENDING,
    FETCH_AUTHORS_FULFILLED,
    FETCH_AUTHORS_REJECTED
} from '../constants/actionTypes';

const fetchAuthorsPending = () => ({ type: FETCH_AUTHORS_PENDING });
const fetchAuthorsFulfilled = (authors) => ({ type: FETCH_AUTHORS_FULFILLED, payload: authors });
const fetchAuthorsRejected = (error) => ({ type: FETCH_AUTHORS_REJECTED, payload: error });

const fetchAuthors = () => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(fetchAuthorsPending());
    console.log('fetchAuthors');

    return axios.get(`${BOOKS_LIBRARY_SERVICE}/author`)
        .then(authors => {
            console.log('axios success: ', authors);
            dispatch(fetchAuthorsFulfilled(authors));
        })
        .catch(error => {
            console.log('axios error: ', error);
            dispatch(fetchAuthorsRejected(error));
        });
};

export default {
    fetchAuthors
};