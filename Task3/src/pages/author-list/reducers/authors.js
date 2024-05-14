import {
    FETCH_AUTHORS_PENDING,
    FETCH_AUTHORS_FULFILLED,
    FETCH_AUTHORS_REJECTED,
 } from '../constants/actionTypes';

const initialState = {
    isLoading: false,
    isError: false,
    authors: [],
    currentPage: 0,
    error: ''
};

export default (state = initialState, action) => {
    switch (action.type) {
        case FETCH_AUTHORS_PENDING: return {
            ...state,
            isLoading: true,
            isError: false,
            error: '',
        }

        case FETCH_AUTHORS_FULFILLED: return {
            ...state,
            isLoading: false,
            isError: false,
            authors: action.payload,
            error: '',
        }

        case FETCH_AUTHORS_REJECTED: return {
            ...state,
            isLoading: false,
            isError: true,
            authors: [],
            error: action.payload,
        }

        default: return state;
    }
};