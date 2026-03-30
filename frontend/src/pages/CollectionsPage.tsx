import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import {
  getCollectionsByUser,
  createCollection,
  deleteCollection,
} from "../features/collection/service/collectionService";

import "../styles/components/CollectionsPage.css";

interface Collection {
  id: number;
  name: string;
}

const CollectionsPage = () => {
  const [collections, setCollections] = useState<Collection[]>([]);
  const [newCollection, setNewCollection] = useState("");
  const navigate = useNavigate();

  const fetchCollections = async () => {
    try {
      const data = await getCollectionsByUser();
      setCollections(data);
    } catch {
      toast.error("Cannot load collections");
    }
  };

  useEffect(() => {
    fetchCollections();
  }, []);

  const handleCreate = async () => {
    if (!newCollection) return;
    try {
      await createCollection(newCollection);
      setNewCollection("");
      fetchCollections();
      toast.success("Created");
    } catch {
      toast.error("Create failed");
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteCollection(id);
      fetchCollections();
      toast.success("Deleted");
    } catch {
      toast.error("Delete failed");
    }
  };

  return (
    <div className="collections-page">
      <h1>Your Collections</h1>

      <div className="create-box">
        <input
          value={newCollection}
          onChange={(e) => setNewCollection(e.target.value)}
          placeholder="Collection name..."
        />
        <button onClick={handleCreate}>Create</button>
      </div>

      <div className="collection-grid">
        {collections.map((c) => (
          <div
            key={c.id}
            className="collection-card"
            onClick={() => navigate(`/collections/${c.id}`)}
          >
            <h3>{c.name}</h3>
            <button
              className="delete-btn"
              onClick={(e) => {
                e.stopPropagation();
                handleDelete(c.id);
              }}
            >
              Delete
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CollectionsPage;
