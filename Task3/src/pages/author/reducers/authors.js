import {
    RESET_SUCCESS,
    RESET_ERROR,
    RESET_AUTHOR,
    CHANGE_CURRENT_PAGE,
    CHANGE_PAGE_SIZE,
    AUTHOR_FIELD_CHANGE,
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

const initialAuthorState = { id: -1, name: '', birthdayYear: '' };

const initialState = {
    isLoading: false,
    isSuccess: false,
    isError: false,
    authors: [],
    currentPage: 0,
    pageSize: 5,
    totalItems: 0,
    totalPages: 0,
    error: '',
    author: initialAuthorState,
};

const authorReducer = (state = initialState, action) => {
    switch (action.type) {
        case RESET_SUCCESS: return {...state, isSuccess: false }
        case RESET_ERROR: return {...state, isError: false, error: '' }   
        case RESET_AUTHOR: return {...state, author: initialAuthorState }   
        case CHANGE_CURRENT_PAGE: return { ...state, currentPage: action.payload }
        case CHANGE_PAGE_SIZE: return { ...state, pageSize: action.payload }
        case AUTHOR_FIELD_CHANGE: return { 
            ...state, 
            author: { ...state.author, ...action.payload }
        }

        case FETCH_AUTHORS_PAGE_PENDING: return {
            ...state,
            isLoading: true,
            isError: false,
            error: '',
            author: initialAuthorState,
        }
        case FETCH_AUTHORS_PAGE_FULFILLED: return {
            ...state,
            isLoading: false,
            error: '',
            author: initialAuthorState,
            authors: action.payload.authors,
            totalItems: action.payload.totalItems,
            totalPages: action.payload.totalPages,
        }
        case FETCH_AUTHORS_PAGE_REJECTED: return {
            ...state,
            isLoading: false,
            isError: true,
            authors: [],
            author: initialAuthorState,
            error: action.payload.error,
        }

        case CREATE_AUTHOR_PENDING: return {
            ...state,
            isLoading: true,
            isSuccess: false,
            isError: false,
            author: action.payload.author,
            error: ''
        }
        case CREATE_AUTHOR_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            author: initialAuthorState,
            authors: [...state.authors, action.payload.author],            
        }
        case CREATE_AUTHOR_REJECTED: return {
            ...state,
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        case FETCH_AUTHOR_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
            author: initialAuthorState,
        }
        case FETCH_AUTHOR_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: false,
            isError: false,
            error: '',
            author: action.payload.author
        }
        case FETCH_AUTHOR_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            author: initialAuthorState,
            error: action.payload.error,
        }

        case UPDATE_AUTHOR_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
        }
        case UPDATE_AUTHOR_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            error: '',
            author: action.payload.author
        }
        case UPDATE_AUTHOR_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        case DELETE_AUTHOR_BY_ID_PENDING: return {
            ...state, 
            isLoading: true,
            isSuccess: false,
            isError: false,          
            error: '',
        }
        case DELETE_AUTHOR_BY_ID_FULFILLED: return {
            ...state,
            isLoading: false,
            isSuccess: true,
            isError: false,
            error: '',
            authors: state.authors.filter(item => item.id !== action.payload.id),
        }
        case DELETE_AUTHOR_BY_ID_REJECTED: return {
            ...state, 
            isLoading: false,
            isSuccess: false,
            isError: true,
            error: action.payload.error,
        }

        default: return state;
    }
};

export default authorReducer;