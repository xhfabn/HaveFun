import React, { useState } from 'react';
import { Upload, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import adminApi from '../api/admin';

const ImageUploader = ({ value, onChange, maxCount = 1 }) => {
  const [uploading, setUploading] = useState(false);

  const handleUpload = async ({ file }) => {
    if (!file) return;
    setUploading(true);
    try {
      const url = await adminApi.uploadImage(file);
      onChange?.(url);
      message.success('图片上传成功');
    } catch (error) {
      console.error(error);
      message.error(error.message || '上传失败');
    } finally {
      setUploading(false);
    }
  };

  const fileList = value
    ? [
        {
          uid: '-1',
          name: 'image',
          status: 'done',
          url: value
        }
      ]
    : [];

  return (
    <Upload
      listType="picture-card"
      fileList={fileList}
      showUploadList={{ showPreviewIcon: true, showRemoveIcon: true }}
      customRequest={handleUpload}
      onRemove={() => onChange?.('')}
      maxCount={maxCount}
      accept="image/*"
      disabled={uploading}
    >
      {fileList.length >= maxCount ? null : (
        <div>
          <PlusOutlined />
          <div style={{ marginTop: 8 }}>{uploading ? '上传中' : '上传'}</div>
        </div>
      )}
    </Upload>
  );
};

export default ImageUploader;