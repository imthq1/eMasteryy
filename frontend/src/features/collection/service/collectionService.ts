import { apiClient } from "@/config/apiClient";

export interface Collection {
  id: number;
  name: string;
}

export const getCollectionsByUser = async (): Promise<Collection[]> => {
  return await apiClient(`/collections/user`);
};

export const createCollection = async (name: string): Promise<Collection> => {
  return await apiClient(`/collections`, "POST", { name });
};

export const deleteCollection = async (collectionId: number): Promise<void> => {
  return await apiClient(`/collections/${collectionId}`, "DELETE");
};
