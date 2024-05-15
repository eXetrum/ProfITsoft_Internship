import axios from '../../../misc/requests';
import config from '../../../config';
import {
    RESET_SUCCESS,
    RESET_ERROR,
    CHANGE_CURRENT_PAGE,
    CHANGE_PAGE_SIZE,
    BOOK_FIELD_CHANGE,
    RESET_BOOK,
    FETCH_BOOKS_PAGE_PENDING,
    FETCH_BOOKS_PAGE_FULFILLED,
    FETCH_BOOKS_PAGE_REJECTED,    
    CREATE_BOOK_PENDING,
    CREATE_BOOK_FULFILLED,
    CREATE_BOOK_REJECTED,
    FETCH_BOOK_BY_ID_PENDING,
    FETCH_BOOK_BY_ID_FULFILLED,
    FETCH_BOOK_BY_ID_REJECTED,
    UPDATE_BOOK_BY_ID_PENDING,
    UPDATE_BOOK_BY_ID_FULFILLED,
    UPDATE_BOOK_BY_ID_REJECTED,
    DELETE_BOOK_BY_ID_PENDING,
    DELETE_BOOK_BY_ID_FULFILLED,
    DELETE_BOOK_BY_ID_REJECTED,
} from '../constants/actionTypes';

const resetSuccess = () => ({ type: RESET_SUCCESS });
const resetError = () => ({ type: RESET_ERROR });
const changeBooksCurrentPage = (currentPage) => ({ type: CHANGE_CURRENT_PAGE, payload: currentPage });
const changeBooksPageSize = (pageSize) => ({ type: CHANGE_PAGE_SIZE, payload: pageSize });
const bookFieldChange = (value) => ({ type: BOOK_FIELD_CHANGE, payload: value });
const resetBook = () => ({ type: RESET_BOOK });

const fetchBooksPagePending = () => ({ type: FETCH_BOOKS_PAGE_PENDING });
const fetchBooksPageFulfilled = ({ books, totalItems, totalPages }) => ({ type: FETCH_BOOKS_PAGE_FULFILLED, payload: { books, totalItems, totalPages } });
const fetchBooksPageRejected = (error) => ({ type: FETCH_BOOKS_PAGE_REJECTED, payload: { error } });

const createBookPending = (book) => ({ type: CREATE_BOOK_PENDING, payload: { book } });
const createBookFulfilled = (book) => ({ type: CREATE_BOOK_FULFILLED, payload: { book } });
const createBookRejected = (error) => ({ type: CREATE_BOOK_REJECTED, payload: { error } });

const fetchBookByIdPending = () => ({ type: FETCH_BOOK_BY_ID_PENDING });
const fetchBookByIdFulfilled = (book) => ({ type: FETCH_BOOK_BY_ID_FULFILLED, payload: { book } });
const fetchBookByIdRejected = (error) => ({ type: FETCH_BOOK_BY_ID_REJECTED, payload: { error } });

const updateBookByIdPending = () => ({ type: UPDATE_BOOK_BY_ID_PENDING });
const updateBookByIdFulfilled = ({ id, book }) => ({ type: UPDATE_BOOK_BY_ID_FULFILLED, payload: { book, id } });
const updateBookByIdRejected = (error) => ({ type: UPDATE_BOOK_BY_ID_REJECTED, payload: { error } });

const deleteBookByIdPending = () => ({ type: DELETE_BOOK_BY_ID_PENDING });
const deleteBookByIdFulfilled = (id) => ({ type: DELETE_BOOK_BY_ID_FULFILLED, payload: { id } });
const deleteBookByIdRejected = (error) => ({ type: DELETE_BOOK_BY_ID_REJECTED, payload: { error } });

// actual requests
const fetchBooksPage = ({ currentPage, pageSize }) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(fetchBooksPagePending());
    return axios.get(`${BOOKS_LIBRARY_SERVICE}/book?page=${currentPage}&size=${pageSize}`)
        .then( response => {
            const { list, totalItems, totalPages } = response;
            dispatch(fetchBooksPageFulfilled({ books: list, totalItems, totalPages }));
        })
        .catch(error => {
            dispatch(fetchBooksPageRejected(error.message));
        });
};

const createBook = (book) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(createBookPending(book));
    return axios.post(`${BOOKS_LIBRARY_SERVICE}/book`, { ...book })
        .then( book => dispatch(createBookFulfilled(book)) )
        .catch( error => dispatch(createBookRejected(error.message)) );
};

const fetchBookById = (id) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(fetchBookByIdPending());
    return axios.get(`${BOOKS_LIBRARY_SERVICE}/book/${id}`)
        .then( book => dispatch(fetchBookByIdFulfilled(book)))
        .catch( error => dispatch(fetchBookByIdRejected(error.message)) );
};

const updateBookById = ({ id, book }) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(updateBookByIdPending({ id, book }));
    return axios.put(`${BOOKS_LIBRARY_SERVICE}/book/${id}`, { ...book })
        .then( response => dispatch(updateBookByIdFulfilled({ id, book })) )
        .catch( error => dispatch(updateBookByIdRejected(error.message)) );
};

const deleteBookById = (id) => (dispatch) => {
    const { BOOKS_LIBRARY_SERVICE } = config;
    dispatch(deleteBookByIdPending());
    return axios.delete(`${BOOKS_LIBRARY_SERVICE}/book/${id}`)
        .then( _ => dispatch(deleteBookByIdFulfilled(id)) )
        .catch( error => dispatch(deleteBookByIdRejected(error.message)) );
};

const actions = {
    resetSuccess,
    resetError,
    changeBooksCurrentPage,
    changeBooksPageSize,
    bookFieldChange,
    resetBook,
    fetchBooksPage,
    createBook,
    fetchBookById,
    updateBookById,
    deleteBookById
};

export default actions;