import http from './http';

export interface DocSpace {
  id: number;
  name: string;
}

export interface DocPage {
  id: number;
  spaceId: number;
  title: string;
  content: string;
  version: number;
}

export interface DocRevision {
  id: number;
  pageId: number;
  version: number;
  content: string;
}

export const createSpace = async (name: string): Promise<DocSpace> => {
  const response = await http.post<DocSpace>('/api/docs/spaces', { name });
  return response.data;
};

export const createPage = async (spaceId: number, title: string, content: string): Promise<DocPage> => {
  const response = await http.post<DocPage>('/api/docs/pages', { spaceId, title, content });
  return response.data;
};

export const updatePage = async (id: number, content: string): Promise<DocPage> => {
  const response = await http.put<DocPage>(`/api/docs/pages/${id}`, { content });
  return response.data;
};

export const listRevisions = async (id: number): Promise<DocRevision[]> => {
  const response = await http.get<DocRevision[]>(`/api/docs/pages/${id}/revisions`);
  return response.data;
};
