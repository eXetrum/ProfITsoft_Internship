import {
    RESET_SUCCESS,
    RESET_ERROR,
    CHANGE_CURRENT_PAGE,
    CHANGE_PAGE_SIZE,
    BOOK_FIELD_CHANGE,
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

const initialBookState = { id: -1, title: '', genre: '', publishYear: '', authorId: '' };

const initialState = {
    isLoading: false,
    isSuccess: false,
    isError: false,
    books: [],
    currentPage: 0,
    pageSize: 5,
    totalItems: 0,
    totalPages: 0,
    error: '',
    book: initialBookState,
};

const bookReducer = (state = initialState, action) => {
    switch (action.type) {
        case RESET_SUCCESS: return {...state, isSuccess: false }
        case RESET_ERROR: return {...state, isError: false, error: '' }        
        case CHANGE_CURRENT_PAGE: return { ...state, currentPage: action.payload }
        case CHANGE_PAGE_SIZE: return { ...state, pageSize: action.payload }
        case BOOK_FIELD_CHANGE: return { 
            ...state, 
            book: { ...state.book, ...action.payload }
        }

        case FETCH_BOOKS_PAGE_PENDING: return {
            ...state,
            isLoading: true,
            isError: false,
            error: '',
            book: initialBookState,
        }
        case FETCH_BOOKS_PAGE_FULFILLED: return {
            ...state,
            isLoading: false,
            error: '',
            book: initialBookState,
            books: action.payload.books,
            totalItems: action.payload.totalItems,
            totalPages: action.payload.totalPages,
        }
        case FETCH_BOOKS_PAGE_REJECTED: return {
            ...state,
            isLoading: false,
            isError: true,
            books: [],
            book: initialBookState,
            error: action.payload.error,
        }

        case CREATE_BOOK_PENDING: return {
            ...state,
            isLoading: true,
            isSuccess: false,
            isError: false,
            book: action.payload.book,
            error: ''
        }
        case CREATE_BOOK_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            book: initialBookState,
            books: [...state.books, action.payload.book],
        }
        case CREATE_BOOK_REJECTED: return {
            ...state,
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        case FETCH_BOOK_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
            book: initialBookState,
        }
        case FETCH_BOOK_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: false,
            isError: false,
            error: '',
            book: action.payload.book
        }
        case FETCH_BOOK_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            book: initialBookState,
            error: action.payload.error,
        }

        case UPDATE_BOOK_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
        }
        case UPDATE_BOOK_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            error: '',
            book: action.payload.book
        }
        case UPDATE_BOOK_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        case DELETE_BOOK_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
        }
        case DELETE_BOOK_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            error: '',
            books: state.books.filter(item => item.id !== action.payload.id),
        }
        case DELETE_BOOK_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        default: return state;
    }
};

export default bookReducer;