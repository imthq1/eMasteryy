import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import toast from "react-hot-toast";
import {
  getWordsByCollection,
  addWord,
  markWordLearned,
  deleteWord,
} from "../features/collection/service/wordService";
import "../styles/components/CollectionDetailPage.css";
import WordDetailModal from "./WordDetailModal";
interface Word {
  id: number;
  word: string;
  learned: boolean;
}

const CollectionDetailPage = () => {
  const { collectionId } = useParams();
  const [words, setWords] = useState<Word[]>([]);
  const [newWord, setNewWord] = useState("");
  const [selectedWord, setSelectedWord] = useState<string | null>(null);

  const fetchWords = async () => {
    try {
      const data = await getWordsByCollection(Number(collectionId));
      setWords(data);
    } catch {
      toast.error("Cannot load words");
    }
  };

  useEffect(() => {
    fetchWords();
  }, [collectionId]);

  const handleAddWord = async () => {
    if (!newWord) return;
    try {
      await addWord(Number(collectionId), newWord);
      setNewWord("");
      fetchWords();
    } catch {
      toast.error("Add word failed");
    }
  };

  const handleLearned = async (id: number) => {
    try {
      await markWordLearned(id);
      fetchWords();
    } catch {
      toast.error("Failed to update status");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteWord(id);
      fetchWords();
    } catch {
      toast.error("Delete failed");
    }
  };

  return (
    <div className="collection-detail">
      <h1>Words</h1>

      <div className="add-word">
        <input
          value={newWord}
          onChange={(e) => setNewWord(e.target.value)}
          placeholder="New word..."
          onKeyDown={(e) => e.key === "Enter" && handleAddWord()}
        />
        <button onClick={handleAddWord}>Add</button>
      </div>

      <div className="word-list">
        {words.map((w) => (
          <div className="word-card" key={w.id}>
            <span
              className={`word-text ${w.learned ? "learned" : ""}`}
              onClick={() => setSelectedWord(w.word)}
            >
              {w.word}
            </span>

            <div className="word-actions">
              <button onClick={() => handleLearned(w.id)}>
                {w.learned ? "Unlearn" : "Learned"}
              </button>
              <button onClick={() => handleDelete(w.id)}>Delete</button>
            </div>
          </div>
        ))}
      </div>

      {selectedWord && (
        <WordDetailModal
          word={selectedWord}
          onClose={() => setSelectedWord(null)}
        />
      )}
    </div>
  );
};

export default CollectionDetailPage;
