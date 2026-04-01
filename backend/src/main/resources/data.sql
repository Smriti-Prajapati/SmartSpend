-- Seed default categories (runs only if table is empty)
INSERT IGNORE INTO categories (id, name, icon, color, type) VALUES
(1,  'Food & Dining',   '🍔', '#f97316', 'EXPENSE'),
(2,  'Travel',          '✈️', '#3b82f6', 'EXPENSE'),
(3,  'Bills & Utilities','💡', '#8b5cf6', 'EXPENSE'),
(4,  'Shopping',        '🛍️', '#ec4899', 'EXPENSE'),
(5,  'Entertainment',   '🎬', '#f59e0b', 'EXPENSE'),
(6,  'Health',          '🏥', '#10b981', 'EXPENSE'),
(7,  'Education',       '📚', '#6366f1', 'EXPENSE'),
(8,  'Transport',       '🚗', '#14b8a6', 'EXPENSE'),
(9,  'Groceries',       '🛒', '#84cc16', 'EXPENSE'),
(10, 'Other',           '📦', '#6b7280', 'EXPENSE'),
(11, 'Salary',          '💼', '#10b981', 'INCOME'),
(12, 'Freelance',       '💻', '#3b82f6', 'INCOME'),
(13, 'Investment',      '📈', '#f59e0b', 'INCOME'),
(14, 'Gift',            '🎁', '#ec4899', 'INCOME'),
(15, 'Other Income',    '💰', '#6b7280', 'INCOME');
