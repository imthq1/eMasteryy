import { apiClient } from "@/config/apiClient";
export interface Word {
  id: number;
  word: string;
  learned: boolean;
}

export const getWordsByCollection = async (
  collectionId: number,
): Promise<Word[]> => {
  return await apiClient(`/words/collection/${collectionId}`);
};

export const addWord = async (
  collectionId: number,
  word: string,
): Promise<Word> => {
  return await apiClient(`/words/collection/${collectionId}`, "POST", {
    word,
  });
};

export const markWordLearned = async (wordId: number): Promise<Word> => {
  return await apiClient(`/words/${wordId}/learned`, "PUT");
};

export const deleteWord = async (wordId: number): Promise<void> => {
  return await apiClient(`/words/${wordId}`, "DELETE");
};
