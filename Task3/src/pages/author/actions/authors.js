import axios from '../../../misc/requests';
import config from '../../../config';
import {
    RESET_SUCCESS,
    RESET_ERROR,
    CHANGE_CURRENT_PAGE,
    CHANGE_PAGE_SIZE,
    AUTHOR_FIELD_CHANGE,
    RESET_AUTHOR,
    FETCH_AUTHORS_PAGE_PENDING,
    FETCH_AUTHORS_PAGE_FULFILLED,
    FETCH_AUTHORS_PAGE_REJECTED,    
    CREATE_AUTHOR_PENDING,
    CREATE_AUTHOR_FULFILLED,
    CREATE_AUTHOR_REJECTED,
    FETCH_AUTHOR_BY_ID_PENDING,
    FETCH_AUTHOR_BY_ID_FULFILLED,
    FETCH_AUTHOR_BY_ID_REJECTED,
    UPDATE_AUTHOR_BY_ID_PENDING,
    UPDATE_AUTHOR_BY_ID_FULFILLED,
    UPDATE_AUTHOR_BY_ID_REJECTED,
    DELETE_AUTHOR_BY_ID_PENDING,
    DELETE_AUTHOR_BY_ID_FULFILLED,
    DELETE_AUTHOR_BY_ID_REJECTED,
} from '../constants/actionTypes';

const resetSuccess = () => ({ type: RESET_SUCCESS });
const resetError = () => ({ type: RESET_ERROR });
const changeAuthorsCurrentPage = (currentPage) => ({ type: CHANGE_CURRENT_PAGE, payload: currentPage });
const changeAuthorsPageSize = (pageSize) => ({ type: CHANGE_PAGE_SIZE, payload: pageSize });
const authorFieldChange = (value) => ({ type: AUTHOR_FIELD_CHANGE, payload: value });
const resetAuthor = () => ({ type: RESET_AUTHOR });

const fetchAuthorsPagePending = () => ({ type: FETCH_AUTHORS_PAGE_PENDING });
const fetchAuthorsPageFulfilled = ({ authors, totalItems, totalPages }) => ({ type: FETCH_AUTHORS_PAGE_FULFILLED, payload: { authors, totalItems, totalPages } });
const fetchAuthorsPageRejected = (error) => ({ type: FETCH_AUTHORS_PAGE_REJECTED, payload: { error } });

const createAuthorPending = (author) => ({ type: CREATE_AUTHOR_PENDING, payload: { author } });
const createAuthorFulfilled = (author) => ({ type: CREATE_AUTHOR_FULFILLED, payload: { author } });
const createAuthorRejected = (error) => ({ type: CREATE_AUTHOR_REJECTED, payload: { error } });

const fetchAuthorByIdPending = () => ({ type: FETCH_AUTHOR_BY_ID_PENDING });
const fetchAuthorByIdFulfilled = (author) => ({ type: FETCH_AUTHOR_BY_ID_FULFILLED, payload: { author } });
const fetchAuthorByIdRejected = (error) => ({ type: FETCH_AUTHOR_BY_ID_REJECTED, payload: { error } });

const updateAuthorByIdPending = () => ({ type: UPDATE_AUTHOR_BY_ID_PENDING });
const updateAuthorByIdFulfilled = ({ id, author }) => ({ type: UPDATE_AUTHOR_BY_ID_FULFILLED, payload: { author, id } });
const updateAuthorByIdRejected = (error) => ({ type: UPDATE_AUTHOR_BY_ID_REJECTED, payload: { error } });

const deleteAuthorByIdPending = () => ({ type: DELETE_AUTHOR_BY_ID_PENDING });
const deleteAuthorByIdFulfilled = (id) => ({ type: DELETE_AUTHOR_BY_ID_FULFILLED, payload: { id } });
const deleteAuthorByIdRejected = (error) => ({ type: DELETE_AUTHOR_BY_ID_REJECTED, payload: { error } });

// actual requests
const fetchAuthorsPage = ({ currentPage, pageSize }) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(fetchAuthorsPagePending());
    return axios.get(`${BOOKS_LIBRARY_SERVICE}/author?page=${currentPage}&size=${pageSize}`)
        .then( response => {
            const { list, totalItems, totalPages } = response;
            dispatch(fetchAuthorsPageFulfilled({ authors: list, totalItems, totalPages }));
        })
        .catch(error => {
            dispatch(fetchAuthorsPageRejected(error.message));
        });
};

const createAuthor = (author) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(createAuthorPending(author));
    return axios.post(`${BOOKS_LIBRARY_SERVICE}/author`, { ...author })
        .then( author => dispatch(createAuthorFulfilled(author)) )
        .catch( error => dispatch(createAuthorRejected(error.message)) );
};

const fetchAuthorById = (id) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(fetchAuthorByIdPending());
    return axios.get(`${BOOKS_LIBRARY_SERVICE}/author/${id}`)
        .then( author => dispatch(fetchAuthorByIdFulfilled(author)))
        .catch( error => dispatch(fetchAuthorByIdRejected(error.message)) );
};

const updateAuthorById = ({ id, author }) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(updateAuthorByIdPending({ id, author }));
    return axios.put(`${BOOKS_LIBRARY_SERVICE}/author/${id}`, { ...author })
        .then( response => dispatch(updateAuthorByIdFulfilled({ id, author })) )
        .catch( error => dispatch(updateAuthorByIdRejected(error.message)) );
};

const deleteAuthorById = (id) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(deleteAuthorByIdPending());
    return axios.delete(`${BOOKS_LIBRARY_SERVICE}/author/${id}`)
        .then( response => {
            console.log('deleteAuthorById result: ', response);
            dispatch(deleteAuthorByIdFulfilled(id));
        })
        .catch( error => dispatch(deleteAuthorByIdRejected(error.message)) );
};

const actions = {
    resetSuccess,
    resetError,
    changeAuthorsCurrentPage,
    changeAuthorsPageSize,
    authorFieldChange,
    resetAuthor,
    fetchAuthorsPage,
    createAuthor,
    fetchAuthorById,
    updateAuthorById,
    deleteAuthorById
};

export default actions;