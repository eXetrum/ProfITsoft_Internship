import requests
import json
import os
import random

MIN_BOOKS_PER_FILE, MAX_BOOKS_PER_FILE = 10, 500
TOTAL_PER_GENRE = 2000
OUTPUT_FOLDER = "json_files"

def get_books():
    genres = ["fiction", "fantasy", "mystery", "romance", "love", "nonfiction", "science_fiction", "historical_fiction", "biography", "poetry"]  # Список жанров

    for genre in genres:
        books = []
        threshold = random.randint(MIN_BOOKS_PER_FILE, MAX_BOOKS_PER_FILE)
        print(f'Collection {threshold} books of {genre} genre...')
        total = 0
        for offset in range(0, TOTAL_PER_GENRE, 5):
            print(f'Current books list size={len(books)}')
            books.extend(get_books_by_genre(genre, offset))
            if len(books) > threshold:
                total += len(books)                
                save_books(books, genre, total)
                print(f'Saving.')
                threshold = random.randint(MIN_BOOKS_PER_FILE, MAX_BOOKS_PER_FILE)
                print(f'Collection {threshold} books of {genre} genre...')
                books = []
            if total >= TOTAL_PER_GENRE:
                break
        if len(books) > 0:
            n += len(books)
            print(f'Saving remaining.')
            save_books_by_decade(books, genre, total)


def get_books(genre, offset):
    url = f"https://openlibrary.org/subjects/{genre}.json?offset={offset}"
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        works = data.get("works", [])
        return works
    else:
        print(f"Unable to get books for genre={genre}. Status code: {response.status_code}")
        return []

def get_books(books, genre, offset):    
    if not os.path.exists(OUTPUT_FOLDER):
        os.makedirs(OUTPUT_FOLDER)
    
    file_name = f"{OUTPUT_FOLDER}/{genre}_{offset}_books.json"
    with open(file_name, "w") as f:
        json.dump(books, f, indent=4)
        print(f"Saved {len(books)} books to file: {file_name}")


if __name__ == "__main__":
    get_books()
