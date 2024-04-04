import json
import os

DATA_FOLDER = "json_files"

def process_file(path):
    try:
        clean_data = []
        with open(path, "r") as f:
            data = json.load(f)
            
            for entry in data:
                clean_data.append({
                    'title': entry['title'], 
                    'subject': entry['subject'], 
                    'authors': [i['name'] for i in entry['authors']],
                    'publish_year': entry['first_publish_year']
                })
        with open(path, "w") as f:
            json.dump(clean_data, f, indent=4)
    except Exception as e:
        print(e)

if __name__ == "__main__":    
    files = [f for f in os.listdir(DATA_FOLDER) if os.path.isfile(os.path.join(DATA_FOLDER, f)) and f.endswith('.json')]
    
    for f in files:
        print(f)
        process_file(os.path.join(DATA_FOLDER, f))
    